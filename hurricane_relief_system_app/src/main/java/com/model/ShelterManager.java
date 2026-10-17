package com.model;

import java.util.ArrayList;
import java.util.List;

public class ShelterManager{
    private ArrayList<Shelter> shelters;

    public ShelterManager(ArrayList<Shelter> shelters){
        this.shelters = shelters;
    }

    public List<Shelter> sortShelters(Location target){
    List<Shelter> sortedShelters = new ArrayList(shelters.values());
    sortedShelters.sort(Comparator.comparingDouble(shelter -> shelter.getLocation().calculateDistance(target)));
    return sortedShelters;
    }
    
    public Shelter findShelter(int id){
        for(Shelter shelter : shelters.values()){
            if(shelter.getId() == id){
                return shelter;
            }
        }
        return null;
    }
}
