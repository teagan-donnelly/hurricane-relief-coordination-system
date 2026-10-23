package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;

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
}
