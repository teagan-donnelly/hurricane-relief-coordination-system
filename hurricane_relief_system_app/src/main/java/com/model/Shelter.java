package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a shelter that people can go to during a hurricane.
 */
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
    private ArrayList<String> occupants = new ArrayList<>();
    private ArrayList<String> pets = new ArrayList<>();

    /**
     * Sets how many pets the shelter can hold and how many it has now.
     * @param petCapacity Max number of pets
     * @param currentPetOccupancy Number of pets in the shelter now
     */
    public void setPetSpace(int petCapacity, int currentPetOccupancy) {
        if (petCapacity < 0 || currentPetOccupancy < 0 || currentPetOccupancy > petCapacity) {
            throw new IllegalArgumentException("Invalid shelter pet capacity or occupancy.");
        }
        this.petCapacity = petCapacity;
        this.currentPetOccupancy = currentPetOccupancy;
    }

    /**
     * Gets how many more pets the shelter can take.
     * @return Number of open pet spots
     */
    public int getAvailablePetSpace() {
        return petCapacity - currentPetOccupancy;
    }

    /**
     * Constructor for a shelter with an admin.
     * @param name Name of the shelter
     * @param id ID of the shelter
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

    /**
     * Constructor for a shelter without an admin.
     * @param name Name of the shelter
     * @param id ID of the shelter
     * @param location Location of the shelter
     * @param occupancy Number of people in the shelter now
     * @param capacity Max number of people
     * @param petFriendly true if pets are allowed
     * @param accessibilityInfo List of accessibility info
     * @param hasPower true if the shelter has power
     * @param status Current status of the shelter
     * @param hasMedicalStaff true if the shelter has medical staff
     */
    public Shelter (String name, UUID id, Location location, int occupancy, int capacity, 
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status, boolean hasMedicalStaff) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this.currentOccupancy = occupancy;
        this.capacity = capacity;
        this.petFriendly = petFriendly;
        this.accessibilityInfo = accessibilityInfo;
        this.hasPower = hasPower;
        this.status = status;
        this.hasMedicalStaff = hasMedicalStaff;
    }

    /**
     * Constructor for a shelter with only the basic info.
     * @param name Name of the shelter
     * @param id ID of the shelter
     * @param location Location of the shelter
     * @param capacity Max number of people
     * @param admin ID of the shelter admin
     */
    public Shelter (String name, UUID id, Location location, int capacity, UUID admin) {
       
        this.name = name;
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.admin = admin;
    
    }

    /**
     * Updates the max number of people the shelter can hold.
     * @param capacity New capacity
     */
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
        for (String member : request.getHouseholdMembers()) {
            if (occupants.contains(member)) {
                System.out.println("This household is already checked in.");
                return false;
            }
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

    /**
     * Adds a person to the shelter's occupant list.
     * @param occupant Name of the person
     */
    public void addOccupant(String occupant) {
        occupants.add(occupant);
    }

    /**
     * Adds a pet to the shelter's pet list.
     * @param pet Name of the pet
     */
    public void addPet(String pet) {
        pets.add(pet);
    }

    /**
     * Sets the admin of the shelter.
     * @param admin ID of the new admin
     */
    public void setAdmin(UUID admin) {
        this.admin = admin;
    }

    /**
     * Checks if a user is the manager of this shelter.
     * @param user the user to check
     * @return true if the user manages this shelter, false otherwise
     */
    public boolean isManagedBy(User user) {
        return user != null && admin != null && admin.equals(user.getUserId());
    }

    //getters
    /**
     * Gets the name of the shelter.
     * @return Name of the shelter
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the shelter allows pets.
     * @return true if pets are allowed, false otherwise
     */
    public boolean isPetFriendly() {
        return petFriendly;
    }

    /**
     * Checks if the shelter has medical staff.
     * @return true if there is medical staff, false otherwise
     */
    public boolean hasMedicalStaff() {
        return hasMedicalStaff;
    }

    /**
     * Gets how many more people the shelter can take.
     * @return Number of open spots
     */
    public int getAvailableSpace() {
        return capacity - currentOccupancy;
    }

    /**
     * Gets the ID of the shelter.
     * @return ID of the shelter
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the location of the shelter.
     * @return Location of the shelter
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the max number of people the shelter can hold.
     * @return Capacity of the shelter
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the number of people in the shelter now.
     * @return Current occupancy
     */
    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    /**
     * Gets the status of the shelter.
     * @return Status of the shelter
     */
    public ShelterStatus getStatus() {
        return status;
    }

    /**
     * Gets the admin of the shelter.
     * @return ID of the shelter admin
     */
    public UUID getAdmin() {
        return admin;
    }

    /**
     * Gets the accessibility info for the shelter.
     * @return List of accessibility info
     */
    public ArrayList<String> getAccessibilityInfo() {
        return accessibilityInfo;
    } 

    /**
     * Checks if the shelter has power.
     * @return true if the shelter has power, false otherwise
     */
    public boolean hasPower() {
        return hasPower;
    }

    /**
     * Gets the people staying in the shelter.
     * @return List of occupants
     */
    public ArrayList<String> getOccupants() {
        return occupants;
    }

    /**
     * Gets the max number of pets the shelter can hold.
     * @return Pet capacity
     */
    public int getPetCapacity() {
        return petCapacity;
    }

    /**
     * Gets the number of pets in the shelter now.
     * @return Current pet occupancy
     */
    public int getCurrentPetOccupancy() {
        return currentPetOccupancy;
    }

    /**
     * Gets the pets staying in the shelter.
     * @return List of pets
     */
    public ArrayList<String> getPets() {
        return pets;
    }

    //setters
    /**
     * Sets the name of the shelter. Not finished yet.
     */
    public void setName(){

    }

    /**
     * Sets the number of people in the shelter.
     * @param currentOccupancy New occupancy
     */
    public void setCurrentoccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    /**
     * Sets the max number of people the shelter can hold.
     * @param capacity New capacity
     */
    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    /**
     * Sets if the shelter allows pets.
     * @param petFriendly true if pets are allowed
     */
    public void setPetFriendly(boolean petFriendly){
        this.petFriendly = petFriendly;
    }

    /**
     * Sets if the shelter has power.
     * @param hasPower true if the shelter has power
     */
    public void setPower(boolean hasPower){
        this.hasPower = hasPower;
    }

    /**
     * Sets the status of the shelter.
     * @param status New status
     */
    public void setStatus(ShelterStatus status){
        this.status = status;
    }

    /**
     * Sets if the shelter has medical staff.
     * @param hasMedicalStaff true if there is medical staff
     */
    public void setMedicalStaff(boolean hasMedicalStaff){
        this.hasMedicalStaff = hasMedicalStaff;
    }

    /**
     * Sets the max number of pets the shelter can hold.
     * @param petCapacity New pet capacity
     */
    public void setPetCapacity(int petCapacity){
        this.petCapacity = petCapacity;
    }

    /**
     * Sets the number of pets in the shelter.
     * @param currentPetOccupancy New pet occupancy
     */
    public void setCurrentPetOccupancy(int currentPetOccupancy){
        this.currentPetOccupancy = currentPetOccupancy;
    }

    /**
     * Sets the list of people in the shelter.
     * @param occupants List of occupants
     */
    public void setOccupants(ArrayList<String> occupants){
        this.occupants = occupants != null ? occupants : new ArrayList<>();
    }

    /**
     * Sets the list of pets in the shelter.
     * @param pets List of pets
     */
    public void setPets(ArrayList<String> pets){
        this.pets = pets != null ? pets : new ArrayList<>();
    }

}
