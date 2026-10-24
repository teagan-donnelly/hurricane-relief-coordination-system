package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.io.FileWriter;
import java.io.IOException;
import com.location.Address;
import com.location.MapTools;

/**
 * @author Nicolas Gauvin
 * Represents a volunteer who can be assigned to help requests.
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

    /**
     * Constructor for a volunteer without a travel distance.
     * @param firstName First name
     * @param lastName Last name
     * @param userID ID of the user
     * @param email Email address
     * @param phone Phone number
     * @param location Location of the volunteer
     * @param birthDate Birth date
     * @param userName Username
     * @param password Password
     * @param notificationPreference How the volunteer wants to be notified
     * @param emergencyContacts List of emergency contacts
     * @param availability true if the volunteer is available
     * @param skills List of skills
     * @param credentials List of credentials
     * @param supplies List of supplies
     * @param isVerified true if the volunteer is verified
     * @param isDispatched true if the volunteer is dispatched
     * @param volunteerHours Number of volunteer hours
     */
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

    /**
     * Constructor for a volunteer with a travel distance.
     * @param firstName First name
     * @param lastName Last name
     * @param userID ID of the user
     * @param email Email address
     * @param phone Phone number
     * @param location Location of the volunteer
     * @param birthDate Birth date
     * @param userName Username
     * @param password Password
     * @param notificationPreference How the volunteer wants to be notified
     * @param emergencyContacts List of emergency contacts
     * @param availability true if the volunteer is available
     * @param travelDistance How far the volunteer can travel
     * @param skills List of skills
     * @param credentials List of credentials
     * @param supplies List of supplies
     * @param isVerified true if the volunteer is verified
     * @param isDispatched true if the volunteer is dispatched
     * @param volunteerHours Number of volunteer hours
     */
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

    /**
     * Constructor for a volunteer with only availability, skills, and credentials.
     * @param availability true if the volunteer is available
     * @param skills List of skills
     * @param credentials List of credentials
     */
    public Volunteer(boolean availability, ArrayList<String> skills, ArrayList<Credential> credentials) {
        super("", "", "", "", "", "");
        this.availability = availability;
        this.skills = skills;
        this.credentials = credentials;
    }

    /**
     * Accepts a help request and marks this volunteer as the one handling it.
     * @param request the request to accept
     */
    public void acceptRequest(HelpRequest request) {
        if (request == null) {
            System.out.println("Request cannot be null.");
            return;
        }

        if (!availability || isDispatched) {
            System.out.println(getFirstName() + " is not available to accept a request right now.");
            return;
        }

        if (request.getRequestStatus() == RequestStatus.CANCELED ||
            request.getRequestStatus() == RequestStatus.COMPLETED) {
            System.out.println("This request is already " + request.getRequestStatus() + ".");
            return;
        }

        isDispatched = true;
        request.setRequestStatus(RequestStatus.ASSIGNED);
        request.setAssignedVolunteerID(getUserId());
        System.out.println(getFirstName() + " accepted the request.");
    }

    /**
     * Declines a help request. If this volunteer was handling it, the request
     * goes back to waiting for another volunteer.
     * @param request the request to decline
     */
    public void declineRequest(HelpRequest request) {
        if (request == null) {
            System.out.println("Request cannot be null.");
            return;
        }

        if (getUserId().equals(request.getAssignedVolunteerID())) {
            isDispatched = false;
            request.setAssignedVolunteerID(null);
            request.setRequestStatus(RequestStatus.AWAITING_ASSIGNMENT);
        }
        System.out.println(getFirstName() + " declined the request.");
    }

    /**
     * Asks another available volunteer for help with a request this volunteer is handling.
     * @param request the request that needs more help
     */
    public void requestAdditionalHelp(HelpRequest request) {
        if (request == null || !getUserId().equals(request.getAssignedVolunteerID())) {
            System.out.println("You can only ask for help on a request you are handling.");
            return;
        }

        for (Volunteer volunteer : VolunteerManager.getInstance().getVolunteers()) {
            if (!volunteer.getUserId().equals(getUserId()) && volunteer.isAvailability() &&
                volunteer.isVerified() && !volunteer.isDispatched()) {
                System.out.println(getFirstName() + " asked for help. " + volunteer.getFirstName() +
                    " " + volunteer.getLastName() + " was notified.");
                return;
            }
        }
        System.out.println("No other volunteers are available to help right now.");
    }

    /**
     * Marks the volunteer as done with their assignment so they can take new ones.
     */
    public void completeAssignment() {
        isDispatched = false;
    }

    /**
     * Adds hours to this volunteer's total.
     * @param hours the number of hours worked
     */
    public void logHours(double hours) {
        if (hours <= 0) {
            System.out.println("Hours must be greater than 0.");
            return;
        }
        volunteerHours += hours;
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

        text.append("============================================================\n");
        text.append("               HURRICANE RELIEF TRIP SHEET\n");
        text.append("============================================================\n");
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

    // --- Getters ---

    /**
     * Checks if the volunteer is available.
     * @return true if available, false otherwise
     */
    public boolean isAvailability() {
        return availability;
    }

    /**
     * Gets how far the volunteer can travel.
     * @return Travel distance
     */
    public double getTravelDistance() {
        return travelDistance;
    }

    /**
     * Gets the volunteer's skills.
     * @return List of skills
     */
    public ArrayList<String> getSkills() {
        return skills;
    }

    /**
     * Gets the volunteer's credentials.
     * @return List of credentials
     */
    public ArrayList<Credential> getCredentials() {
        return credentials;
    }

    /**
     * Gets the volunteer's supplies.
     * @return List of supplies
     */
    public ArrayList<String> getSupplies() {
        return supplies;
    }

    /**
     * Checks if the volunteer is verified.
     * @return true if verified, false otherwise
     */
    public boolean isVerified() {
        return isVerified;
    }

    /**
     * Checks if the volunteer is dispatched.
     * @return true if dispatched, false otherwise
     */
    public boolean isDispatched() {
        return isDispatched;
    }

    /**
     * Gets the volunteer's total hours.
     * @return Number of volunteer hours
     */
    public double getVolunteerHours() {
        return volunteerHours;
    }

    /**
     * Sets if the volunteer is dispatched.
     * @param isDispatched true if the volunteer is dispatched
     */
    public void setDispatched(boolean isDispatched) { this.isDispatched = isDispatched; }
}