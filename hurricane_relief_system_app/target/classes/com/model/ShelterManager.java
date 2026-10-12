package com.model;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class ShelterManager {
   private HashMap<UUID, Shelter> shelters;

   public ShelterManager(HashMap<UUID, Shelter> shelters) {
      this.shelters = shelters;
   }

   public List<Shelter> sortShelters(Location target) {
      List<Shelter> sortedShelters = new ArrayList<>(shelters.values());

      sortedShelters.sort(comparator.comparingDouble(shelter -> shelter.getLocation().calculateDistance(target)));
      return sortedShelters;
   }

   public Shelter findShelter(int id) {
      for(Shelter shelter : shelters.values()){
        if(shelter.getId() == id){
            return shelter;
        }
      }
      return null;
   }
}

