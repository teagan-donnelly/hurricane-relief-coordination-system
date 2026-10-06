package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Nicolas Gauvin
 */
public class Volunteer extends User {

    private boolean availability;
    private double travelDistance;
    private ArrayList<String> skills;
    private ArrayList<Credential> credentials;
    private ArrayList<String> supplies;
    private boolean isVerified;
    private boolean isDispatched;
    private double volunteerHours;

    public Volunteer(String firstName, String lastName, UUID userID, String email, Location location,
                     LocalDate birthDate, String userName, String password, NotificationType notificationPreference,
                     ArrayList<String> emergencyContacts, boolean availability, ArrayList<String> skills,
                     ArrayList<Credential> credentials, ArrayList<String> supplies, boolean isVerified,
                     boolean isDispatched, double volunteerHours) {
        
        super(firstName, lastName, userID, email, location, birthDate, userName, password, notificationPreference, emergencyContacts);
        
        this.availability = availability;
        this.skills = skills;
        this.credentials = credentials;
        this.supplies = supplies;
        this.isVerified = isVerified;
        this.isDispatched = isDispatched;
        this.volunteerHours = volunteerHours;
    }

    public Volunteer(String firstName, String lastName, UUID userID, String email, Location location,
                     LocalDate birthDate, String userName, String password, NotificationType notificationPreference,
                     ArrayList<String> emergencyContacts, boolean availability, double travelDistance,
                     ArrayList<String> skills, ArrayList<Credential> credentials, ArrayList<String> supplies,
                     boolean isVerified, boolean isDispatched, double volunteerHours) {
        
        super(firstName, lastName, userID, email, location, birthDate, userName, password, notificationPreference, emergencyContacts);
        
        this.availability = availability;
        this.travelDistance = travelDistance;
        this.skills = skills;
        this.credentials = credentials;
        this.supplies = supplies;
        this.isVerified = isVerified;
        this.isDispatched = isDispatched;
        this.volunteerHours = volunteerHours;
    }

    public Volunteer(boolean availability, ArrayList<String> skills, ArrayList<Credential> credentials) {
        super("", "", null, "");
        this.availability = availability;
        this.skills = skills;
        this.credentials = credentials;
    }

    public void acceptRequest(HelpRequest request) {

    }

    public void declineRequest(HelpRequest request) {

    }

    public void requestAdditionalHelp(HelpRequest request) {

    }

    public void completeAssignment() {

    }

    public void logHours() {

    }

    // --- Getters ---

    public boolean isAvailability() {
        return availability;
    }

    public double getTravelDistance() {
        return travelDistance;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public ArrayList<Credential> getCredentials() {
        return credentials;
    }

    public ArrayList<String> getSupplies() {
        return supplies;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public boolean isDispatched() {
        return isDispatched;
    }

    public double getVolunteerHours() {
        return volunteerHours;
    }
}