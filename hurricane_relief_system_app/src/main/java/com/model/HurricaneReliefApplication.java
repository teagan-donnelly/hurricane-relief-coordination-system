package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The main application class for managing hurricane relief operations.
 * @author APR
 */
public class HurricaneReliefApplication {

    private static HurricaneReliefApplication instance;

    private RequestManager requestManager;
    private VolunteerManager volunteerManager;
    private ShelterManager shelterManager;
    private AccountManager accountManager;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the managers for requests, volunteers, shelters, and accounts.
     */

    private HurricaneReliefApplication() {
        requestManager = RequestManager.getInstance();
        volunteerManager = VolunteerManager.getInstance();
        shelterManager = new ShelterManager(DataLoader.getShelters());
        accountManager = new AccountManager(DataLoader.getAllUsers()); 
    }

    /**
     * Returns the singleton instance of the HurricaneReliefApplication.
     * @return the singleton instance of the HurricaneReliefApplication
     */

    public static HurricaneReliefApplication getInstance() {
        return instance != null ? instance : (instance = new HurricaneReliefApplication());
    }

    /**
     * Submits a help request to the system.
     * @param requestID the unique identifier for the help request
     * @param requestType the type of the help request
     * @param user the user who submitted the request
     * @param comment a comment describing the help needed
     * @param assistanceType the type of assistance required
     * @param urgency the urgency level of the request
     * @param requestStatus the current status of the request
     * @param location the location where help is needed
     * @param photo an optional photo related to the request
     */

    public void submitHelpRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo) {

        HelpRequest helpRequest = new HelpRequest(requestID, requestType, user, comment, 
            assistanceType, urgency, requestStatus, location, photo);
        requestManager.addRequest(helpRequest);
        
    }

    /**
     * Finds and returns a list of available volunteers in the system.
     * @return a list of available volunteers
     */

    public List<Volunteer> findAvailableVolunteers() {
        ArrayList<Volunteer> volunteers = DataLoader.getVolunteers();
        ArrayList<Volunteer> availableVolunteers = new ArrayList<>();
        for (Volunteer volunteer : volunteers) {
            if (volunteer.isAvailability()) {
                availableVolunteers.add(volunteer);
            }
        }
        return availableVolunteers;
    }

    /**
     * Finds and returns a list of available shelters in the system.
     * @return a list of available shelters
     */

    public List<Shelter> findShelters() {
        ArrayList<Shelter> shelters = DataLoader.getShelters();
        ArrayList<Shelter> availableShelters = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getStatus() == ShelterStatus.OPEN) {
                availableShelters.add(shelter);
            }
        }
        return availableShelters;
    }

    /**
     * Registers a new user in the system.
     * @param type the type of user to register
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email address of the user
     * @param location the location of the user
     * @param birthDate the birth date of the user
     * @param availability the availability status of the user
     * @param travelDistance the maximum travel distance for the user
     * @param skills a list of skills possessed by the user
     * @param credentials a list of credentials for the user
     * @param supplies a list of supplies available to the user
     * @param isVerified indicates if the user is verified
     * @param isDispached indicates if the user is dispatched
     * @param volunteerHours the number of volunteer hours for the user
     * @param householdSize the size of the user's household
     * @param assistanceType the type of assistance needed by the user
     * @param pastRequests a list of past requests made by the user
     */
    public void registerUser(User type, String firstName, String lastName, String email, Location location, LocalDate birthDate,

        boolean availability, double travelDistance, ArrayList<String> skills, ArrayList<Credential> credentials,  
        ArrayList<String> supplies, boolean isVerified, boolean isDispached, double volunteerHours,

        int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {
            
        AccountManager.createUser(type, firstName, lastName, email, location, birthDate,
            availability, travelDistance, skills, credentials, supplies, isVerified, isDispached, volunteerHours,
            householdSize, assistanceType, pastRequests);
    }

    /**
     * Retrieves the status of a help request based on its unique identifier.
     * @param requestID the unique identifier of the help request
     * @return the status of the help request, or null if not found
     */
    public RequestStatus getRequestStatus(UUID requestID) {
        ArrayList<HelpRequest> requests = DataLoader.getRequests();

        for (HelpRequest request : requests) {
            if(request.getRequestID().equals(requestID)) return request.getRequestStatus();
        }

        System.out.println("Could not find a request matching the given ID.");
        return null;
    }

    /**
     * Attempts to log in a user with the provided credentials.
     * @param username the username of the user
     * @param password the password of the user
     * @return true if login is successful, false otherwise
     */
    public boolean login(String username, String password) {
        if (accountManager.login(username, password)) {
            System.out.println("Login successful.");
            return true;
        }
        System.out.println("Login failed. Invalid username or password.");
        return false;
    }

    /**
     * Logs out the currently logged-in user.
     */
    public void logout() {
        if (accountManager.logout()) {
            System.out.println("Logout successful.");
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

}
