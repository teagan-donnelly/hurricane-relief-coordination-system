package com.model;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import com.location.Address;
import com.location.MapTools;

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

    public Volunteer(String firstName, String lastName, UUID userID, String email, String phone, Location location,
                     LocalDate birthDate, String userName, String password, NotificationType notificationPreference,
                     ArrayList<String> emergencyContacts, boolean availability, ArrayList<String> skills,
                     ArrayList<Credential> credentials, ArrayList<String> supplies, boolean isVerified,
                     boolean isDispatched, double volunteerHours) {
        
        super(firstName, lastName, userID, email, phone, userName, password, location, birthDate, notificationPreference, emergencyContacts);
        
        this.availability = availability;
        this.skills = skills;
        this.credentials = credentials;
        this.supplies = supplies;
        this.isVerified = isVerified;
        this.isDispatched = isDispatched;
        this.volunteerHours = volunteerHours;
    }

    public Volunteer(String firstName, String lastName, UUID userID, String email, String phone, Location location,
                     LocalDate birthDate, String userName, String password, NotificationType notificationPreference,
                     ArrayList<String> emergencyContacts, boolean availability, double travelDistance,
                     ArrayList<String> skills, ArrayList<Credential> credentials, ArrayList<String> supplies,
                     boolean isVerified, boolean isDispatched, double volunteerHours) {
        
        super(firstName, lastName, userID, email, phone, userName, password, location, birthDate, notificationPreference, emergencyContacts);
        
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
        super("", "", "", "", "", "");
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

    /**
     * Exports a printable trip sheet for a request this volunteer is handling.
     * @param request the request being transported
     * @param shelter the shelter the family is going to
     * @param fileName the name of the .txt file to create
     * @return true if the file was written, false otherwise
     */
    public boolean exportTripInfo(PersonalRequest request, Shelter shelter, String fileName) {
        if (request == null || shelter == null) {
            System.out.println("A request and a shelter are needed to export a trip.");
            return false;
        }

        String line = "------------------------------------------------------------\n";
        User requester = request.getUser();
        StringBuilder text = new StringBuilder();

        text.append("               HURRICANE RELIEF TRIP SHEET\n");
        text.append("Volunteer: " + getFirstName() + " " + getLastName() + " (" + getPhone() + ")\n");
        if (getSupplies() != null && !getSupplies().isEmpty()) {
            text.append("Vehicle/Supplies: " + String.join(", ", getSupplies()) + "\n");
        }
        text.append("Request status: " + request.getRequestStatus() + "\n\n");

        text.append("PASSENGERS (" + request.getHouseholdSize() + " people)\n");
        text.append(line);
        for (String member : request.getHouseholdMembers()) {
            text.append("  - " + member + "\n");
        }
        text.append("\nPETS (" + request.getPets() + ")\n");
        text.append(line);
        for (String pet : request.getPetNames()) {
            text.append("  - " + pet + "\n");
        }

        text.append("\nCONTACT INFORMATION\n");
        text.append(line);
        if (requester != null) {
            text.append("  Name:  " + requester.getFirstName() + " " + requester.getLastName() + "\n");
            text.append("  Phone: " + requester.getPhone() + "\n");
            text.append("  Email: " + requester.getEmail() + "\n");
            for (String contact : requester.getEmergencyContacts()) {
                text.append("  Also:  " + contact + "\n");
            }
        }

        text.append("\nPICKUP ADDRESS\n");
        text.append(line);
        text.append("  " + toAddress(request.getLocation()) + "\n");
        if (request.getAccessInstructions() != null && !request.getAccessInstructions().isEmpty()) {
            text.append("  Note: " + request.getAccessInstructions() + "\n");
        }

        if (!request.getMedicalNote().isEmpty()) {
            text.append("\n*** MEDICAL NOTE ***\n");
            text.append(line);
            text.append("  " + request.getMedicalNote() + "\n");
        }

        text.append("\nDIRECTIONS: YOUR LOCATION TO PICKUP\n");
        text.append(line);
        text.append(getDirections(getLocation(), request.getLocation()));

        text.append("\nDIRECTIONS: PICKUP TO SHELTER\n");
        text.append(line);
        text.append(getDirections(request.getLocation(), shelter.getLocation()));

        text.append("\nDESTINATION SHELTER\n");
        text.append(line);
        text.append("  " + shelter.getName() + "\n");
        text.append("  " + toAddress(shelter.getLocation()) + "\n");

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(text.toString());
            System.out.println("Trip information exported to " + fileName);
            return true;
        } catch (IOException e) {
            System.out.println("Could not export the trip information.");
            return false;
        }
    }

    /**
     * Gets numbered driving directions between two locations.
     * @param start where the drive starts
     * @param end where the drive ends
     * @return the directions as text, or a message if they could not be loaded
     */
    private String getDirections(Location start, Location end) {
        String text = "";
        try {
            String[] steps = MapTools.getInstance().getDrivingDirections(toAddress(start), toAddress(end));
            for (int i = 0; i < steps.length; i++) {
                text += "  " + (i + 1) + ". " + steps[i] + "\n";
            }
        } catch (Exception e) {
            text += "  Directions could not be loaded (" + e.getMessage() + ").\n";
        }
        return text;
    }

    /**
     * Converts a Location into an Address for the map tools.
     * @param location the location to convert
     * @return the matching address
     */
    private Address toAddress(Location location) {
        return new Address(location.getStreetAddress(), location.getCity(),
            location.getState(), location.getZipcode());
    }

    // Getters

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

    public void setDispatched(boolean isDispatched) { this.isDispatched = isDispatched; }
}