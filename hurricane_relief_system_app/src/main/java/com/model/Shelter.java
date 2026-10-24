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
    private boolean hasMedicalStaff;
    private int petCapacity;
    private int currentPetOccupancy;
    private UUID admin;
    private ArrayList<AidRequester> occupants;

    public void setPetSpace(int petCapacity, int currentPetOccupancy) {
        if (petCapacity < 0 || currentPetOccupancy < 0 || currentPetOccupancy > petCapacity) {
            throw new IllegalArgumentException("Invalid shelter pet capacity or occupancy.");
        }
        this.petCapacity = petCapacity;
        this.currentPetOccupancy = currentPetOccupancy;
    }

    public int getAvailablePetSpace() {
        return petCapacity - currentPetOccupancy;
    }

    public Shelter (String name, UUID id, Location location, int currentOccupancy, int capacity, 
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status,
        boolean hasMedicalStaff, UUID admin) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this.hasMedicalStaff = hasMedicalStaff;
        this.currentOccupancy = currentOccupancy;
        this.capacity = capacity;
        this.petFriendly = petFriendly;
        this.accessibilityInfo = accessibilityInfo;
        this.hasPower = hasPower;
        this.status = status;
        this.admin = admin;
    }

    public Shelter (String name, UUID id, Location location, int capacity, 
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status, UUID admin) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.petFriendly = petFriendly;
        this.accessibilityInfo = accessibilityInfo;
        this.hasPower = hasPower;
        this.status = status;
        this.admin = admin;
    }

    public Shelter (String name, UUID id, Location location, int capacity, UUID admin) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.admin = admin;
    
    }

    public void updateCapacity(int capacity) {
        this.capacity = capacity;
    }

    //getters
    public String getName() {
        return name;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public boolean hasMedicalStaff() {
        return hasMedicalStaff;
    }

    public int getAvailableSpace() {
        return capacity - currentOccupancy;
    }

    public UUID getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public ShelterStatus getStatus() {
        return status;
    }

    public UUID getAdmin() {
        return admin;
    }

}
