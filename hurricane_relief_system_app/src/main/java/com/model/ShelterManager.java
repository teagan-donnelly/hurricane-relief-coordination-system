package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;
import com.location.DrivingDistanceService;
import java.io.IOException;

public class ShelterManager{
    private ArrayList<Shelter> shelters;

    public ShelterManager(ArrayList<Shelter> shelters){
        this.shelters = shelters;
    }

    public List<Shelter> sortShelters(Location target){
    List<Shelter> sortedShelters = new ArrayList<>(shelters);
    sortedShelters.sort(Comparator.comparingDouble(shelter -> shelter.getLocation().calculateDistance(target)));
    return sortedShelters;
    }

    public Shelter findShelter(UUID id){
        for(Shelter shelter : shelters){
            if(shelter.getId().equals(id)){
                return shelter;
            }
        }
        return null;
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

    protected double getDrivingDistance(Location start, Location end)
            throws IOException, InterruptedException {
        return new DrivingDistanceService().getDistance(start.getLatitude(), start.getLongitude(),
            end.getLatitude(), end.getLongitude());
    }

    public ArrayList<Shelter> getShelters() {
        return shelters;
    }
}
