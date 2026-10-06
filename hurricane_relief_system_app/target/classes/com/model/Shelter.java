package com.model;

/**
 * @author: Cameron Seymore
 */
public class Shelter {

    private String name;
    private int id;
    private String location;
    private int currentoccupancy;
    private int capacity;
    private boolean petFriendly;
    private ArrayList<String> accessibilityInfo;
    private boolean hasPower;
    private ArrayList<Donation> resources;

    public Shelter(String name, int id, String location, int currentoccupancy, int capacity, boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.currentoccupancy = currentoccupancy;
        this.capacity = capacity;
        this.petFriendly = petFriendly;
        this.accessibilityInfo = accessibilityInfo;
        this.hasPower = hasPower;
        this.status = status;
    }

    public Shelter(String name, String location, int capacity, boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.petFriendly = petFriendly;
        this.accessibilityInfo = accessibilityInfo;
        this.hasPower = hasPower;
        this.status = status;
    }

    public Shelter(String name, String location, int capacity){
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }

    public void updateCapacity(int capacity) {
        this.capacity = capacity;
    }
}
