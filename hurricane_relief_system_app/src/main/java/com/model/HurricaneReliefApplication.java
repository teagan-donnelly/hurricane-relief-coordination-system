package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HurricaneReliefApplication {

    private static HurricaneReliefApplication instance;

    private RequestManager requestManager;
    private VolunteerManager volunteerManager;
    private ShelterManager shelterManager;
    private AccountManager accountManager;

    private HurricaneReliefApplication() {
        requestManager = new RequestManager();   
        volunteerManager = new VolunteerManager();
        shelterManager = new ShelterManager(DataLoader.getShelters());
        accountManager = new AccountManager(DataLoader.getAllUsers()); 
    }

    public static HurricaneReliefApplication getInstance() {
        return instance != null ? instance : (instance = new HurricaneReliefApplication());
    }

    public void submitHelpRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo) {

        HelpRequest helpRequest = new HelpRequest(requestID, requestType, user, comment, 
            assistanceType, urgency, requestStatus, location, photo);
        RequestManager.addRequest(helpRequest);
        
    }

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

    public void registerUser(User type, String firstName, String lastName, String email, Location location, LocalDate birthDate,

        boolean availability, double travelDistance, ArrayList<String> skills, ArrayList<Credential> credentials,  
        ArrayList<String> supplies, boolean isVerified, boolean isDispached, double volunteerHours,

        int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {
            
        AccountManager.createUser(type, firstName, lastName, email, location, birthDate,
            availability, travelDistance, skills, credentials, supplies, isVerified, isDispached, volunteerHours,
            householdSize, assistanceType, pastRequests);
    }

    public RequestStatus getRequestStatus(UUID requestID) {
        ArrayList<HelpRequest> requests = DataLoader.getRequests();

        for (HelpRequest request : requests) {
            if(request.getRequestID().equals(requestID)) return request.getRequestStatus();
        }

        System.out.println("Could not find a request matching the given ID.");
        return null;
    }

    public boolean login(String username, String password) {
        if (accountManager.login(username, password)) {
            System.out.println("Login successful.");
            return true;
        }
        System.out.println("Login failed. Invalid username or password.");
        return false;
    }

    public void logout() {
        if (accountManager.logout()) {
            System.out.println("Logout successful.");
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public static void main(String[] args) {
        HurricaneReliefApplication app = HurricaneReliefApplication.getInstance();
        // Example usage of the application
        app.login("jdoe88", "hashed_password_123");
        // Other operations...
        app.logout();
    }


}