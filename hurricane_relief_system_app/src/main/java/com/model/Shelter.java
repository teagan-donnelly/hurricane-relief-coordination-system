package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class Shelter {

    private String name;
    private UUID id;
    private Location location;
    private int currentOccupancy;
    private int capacity;
    private boolean petFriendly;
    private ArrayList<String> accessibilityInfo;
    private boolean hasPower;
    private ShelterStatus status;

    public Shelter (String name, UUID id, Location location, int currentOccupancy, int capacity, 
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this,currentOccupancy = currentOccupancy;
        this.capacity = capacity;
        this.petFriendly = petFriendly;
        this.accessibilityInfo = accessibilityInfo;
        this.hasPower = hasPower;
        this.status = status;
    }

    public Shelter (String name, UUID id, Location location, int capacity, 
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.petFriendly = petFriendly;
        this.accessibilityInfo = accessibilityInfo;
        this.hasPower = hasPower;
        this.status = status;
    }

    public Shelter (String name, UUID id, Location location, int capacity) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this.capacity = capacity;
    
    }

    public void updateCapacity(int capacity) {
        this.capacity = capacity;
    }

}