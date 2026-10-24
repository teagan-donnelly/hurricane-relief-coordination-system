package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;
import com.location.DrivingDistanceService;
import java.io.IOException;

/**
 * Manages the list of shelters in the system.
 */
public class ShelterManager{
    private ArrayList<Shelter> shelters;

    /**
     * Constructor that loads the shelters from the JSON file.
     */
    public ShelterManager(){
        this.shelters = DataLoader.getShelters();
    }

    /**
     * Sorts the shelters from closest to farthest from a location.
     * @param target Location to compare the shelters to
     * @return Sorted list of shelters
     */
    public List<Shelter> sortShelters(Location target){
    List<Shelter> sortedShelters = new ArrayList<>(shelters);
    sortedShelters.sort(Comparator.comparingDouble(shelter -> shelter.getLocation().calculateDistance(target)));
    return sortedShelters;
    }

    /**
     * Finds a shelter by its ID.
     * @param id ID of the shelter
     * @return The shelter, or null if it was not found
     */
    public Shelter findShelter(UUID id){
        for(Shelter shelter : shelters){
            if(shelter.getId().equals(id)){
                return shelter;
            }
        }
        return null;
    }

    /**
     * Creates a new shelter and adds it to the list.
     * @param name Name of the shelter
     * @param location Location of the shelter
     * @param currentOccupancy Number of people in the shelter now
     * @param capacity Max number of people
     * @param petFriendly true if pets are allowed
     * @param accessibilityInfo List of accessibility info
     * @param hasPower true if the shelter has power
     * @param status Current status of the shelter
     * @param hasMedicalStaff true if the shelter has medical staff
     * @param admin ID of the shelter admin
     */
    public static void registerShelter(String name, Location location, int currentOccupancy, int capacity, 
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status,
        boolean hasMedicalStaff, UUID admin) {

        ShelterManager manager = new ShelterManager();
        ArrayList<Shelter> shelters = manager.getShelters();
        UUID id = UUID.randomUUID(); 

        Shelter shelter = new Shelter(name, id, location, currentOccupancy, capacity, 
                    petFriendly, accessibilityInfo, hasPower, status, hasMedicalStaff, admin);
        shelters.add(shelter);
        DataWriter.saveShelter(shelter);

    }

    /**
     * Gets a report of a shelter's info.
     * @param id ID of the shelter
     * @return Report of the shelter, or a message if it was not found
     */
    public static String viewShelter(UUID id) {
        ShelterManager manager = new ShelterManager();
        ArrayList<Shelter> shelters = manager.getShelters();

        for (Shelter shelter : shelters) {
            if (shelter.getId().equals(id)) {
                String report = "Shelter name" + shelter.getName() + "\n" +
                    "Shelter location" + shelter.getLocation() + "\n" +
                    "Shelter capacity" + shelter.getCapacity() + "\n" +
                    "Shelter occupancy" + shelter.getCurrentOccupancy() + "\n" +
                    "Shelter space" + shelter.getAvailableSpace() + "\n" +
                    "Shelter occupants" + shelter.getOccupants();

                return report;

            }
        }
        return "Could not find shelter";

    }

    /**
     * Edits the info of a shelter.
     * @param id ID of the shelter
     * @param occupancyChange Change in the number of people
     * @param petFriendly true if pets are allowed
     * @param PetOccupancyChange Change in the number of pets
     * @param hasPower true if the shelter has power
     * @param status New status of the shelter
     * @param hasMedicalStaff true if the shelter has medical staff
     */
    public static void editShelter(UUID id, int occupancyChange, boolean petFriendly, int PetOccupancyChange,
        boolean hasPower, ShelterStatus status, boolean hasMedicalStaff) {

        ShelterManager manager = new ShelterManager();
        ArrayList<Shelter> shelters = manager.getShelters();

        for (Shelter shelter : shelters) {
            if (shelter.getId().equals(id)) {
                int currentoccupancy = shelter.getCurrentOccupancy();
                int currentPetOccupancy = shelter.getCurrentPetOccupancy();
                shelter.setCurrentoccupancy(currentoccupancy + occupancyChange);
                shelter.setPetFriendly(petFriendly);
                shelter.setCurrentPetOccupancy(currentPetOccupancy + PetOccupancyChange);
                shelter.setPower(hasPower);
                shelter.setStatus(status);
                shelter.setMedicalStaff(hasMedicalStaff);
                DataWriter.saveShelter(shelter);
            }
        }

    }

    /**
     * Explains why a shelter can't take a request.
     * @param shelter the shelter to check
     * @param request the request that needs a shelter
     * @param needsMedical true if someone in the household needs medical staff
     * @return the reason it doesn't work, or null if the shelter is suitable
     */
    public String getProblem(Shelter shelter, PersonalRequest request, boolean needsMedical) {
        if (shelter.getStatus() != ShelterStatus.OPEN) {
            return "not open (" + shelter.getStatus() + ")";
        }
        if (request.getPets() > 0 && !shelter.isPetFriendly()) {
            return "does not accept pets";
        }
        if (needsMedical && !shelter.hasMedicalStaff()) {
            return "no medical staff";
        }
        if (shelter.getAvailablePetSpace() < request.getPets()) {
            return "not enough pet spaces";
        }
        if (shelter.getAvailableSpace() < request.getHouseholdSize()) {
            int space = shelter.getAvailableSpace();
            return "only " + space + (space == 1 ? " space" : " spaces") + " left";
        }
        return null;
    }

    /**
     * Finds the suitable shelter closest to a request by driving distance.
     * @param request the request that needs a shelter
     * @param needsMedical true if someone in the household needs medical staff
     * @return the closest suitable shelter, or null if none are suitable
     */
    public Shelter findClosestSuitableShelter(PersonalRequest request, boolean needsMedical)
            throws IOException, InterruptedException {
        Shelter closest = null;
        double shortestDistance = Double.MAX_VALUE;
        for (Shelter shelter : shelters) {
            if (getProblem(shelter, request, needsMedical) == null) {
                double distance = getDrivingDistance(request.getLocation(), shelter.getLocation());
                System.out.printf("  %s: %.2f km by road%n", shelter.getName(), distance / 1000);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closest = shelter;
                }
            }
        }
        return closest;
    }

    /**
     * Gets the driving distance between two locations.
     * @param start Starting location
     * @param end Ending location
     * @return Driving distance in meters
     * @throws IOException if the lookup fails
     * @throws InterruptedException if the lookup is interrupted
     */
    protected double getDrivingDistance(Location start, Location end)
            throws IOException, InterruptedException {
        return new DrivingDistanceService().getDistance(start.getLatitude(), start.getLongitude(),
            end.getLatitude(), end.getLongitude());
    }

    /**
     * Gets the list of shelters.
     * @return List of shelters
     */
    public ArrayList<Shelter> getShelters() {
        return shelters;
    }
}
