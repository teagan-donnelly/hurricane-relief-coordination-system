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

    /**
     * Checks a household into the shelter.
     * @param request the request for the household arriving
     * @return true if they were checked in, false otherwise
     */
    public boolean checkIn(PersonalRequest request) {
        if (request == null) {
            return false;
        }
        if (status != ShelterStatus.OPEN) {
            System.out.println(name + " is not open.");
            return false;
        }
        if (!request.getHouseholdMembers().isEmpty() &&
            occupants.contains(request.getHouseholdMembers().get(0))) {
            System.out.println("This household is already checked in.");
            return false;
        }
        if (getAvailableSpace() < request.getHouseholdSize()) {
            System.out.println("Not enough space for this household.");
            return false;
        }
        if (request.getPets() > 0 && (!petFriendly || getAvailablePetSpace() < request.getPets())) {
            System.out.println("This shelter cannot take the household's pets.");
            return false;
        }

        for (String member : request.getHouseholdMembers()) {
            occupants.add(member);
        }
        currentOccupancy += request.getHouseholdSize();

        for (String pet : request.getPetNames()) {
            pets.add(pet);
        }
        currentPetOccupancy += request.getPets();

        if (currentOccupancy >= capacity) {
            status = ShelterStatus.FULL;
        }
        return true;
    }

    public void addOccupant(String occupant) {
        occupants.add(occupant);
    }

    public void addPet(String pet) {
        pets.add(pet);
    }

    public void setAdmin(User admin) {
        this.admin = admin;
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
