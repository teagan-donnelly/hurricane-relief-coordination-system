package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;
import com.location.Address;
import com.location.MapTools;

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
        if (shelter.getAvailableSpace() < request.getHouseholdSize()) {
            int space = shelter.getAvailableSpace();
            return "only " + space + (space == 1 ? " space" : " spaces") + " left";
        }
        return null;
    }

    /**
     * Finds the suitable shelter closest to a request by driving distance.
     * Falls back to straight-line distance if the map service can't be reached.
     * @param request the request that needs a shelter
     * @param needsMedical true if someone in the household needs medical staff
     * @return the closest suitable shelter, or null if none are suitable
     */
    public Shelter findClosestSuitableShelter(PersonalRequest request, boolean needsMedical) {
        ArrayList<Shelter> suitable = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (getProblem(shelter, request, needsMedical) == null) {
                suitable.add(shelter);
            }
        }

        if (suitable.isEmpty()) {
            return null;
        }

        Address[] addresses = new Address[suitable.size()];
        for (int i = 0; i < suitable.size(); i++) {
            addresses[i] = toAddress(suitable.get(i).getLocation());
        }

        try {
            Address closest = MapTools.getInstance().findClosestAddress(toAddress(request.getLocation()), addresses);
            for (int i = 0; i < addresses.length; i++) {
                if (addresses[i] == closest) {
                    return suitable.get(i);
                }
            }
        } catch (Exception e) {
            System.out.println("Map service unavailable (" + e.getMessage() + "). Using straight-line distance.");
        }

        suitable.sort(Comparator.comparingDouble(shelter -> shelter.getLocation().calculateDistance(request.getLocation())));
        return suitable.get(0);
    }

    private Address toAddress(Location location) {
        return new Address(location.getStreetAddress(), location.getCity(),
            location.getState(), location.getZipcode());
    }

    public ArrayList<Shelter> getShelters() {
        return shelters;
    }
}
