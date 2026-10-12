package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HurricaneReliefApplication {

    private static HurricaneReliefApplication instance;

    //private RequestManager requestManager;
    private VolunteerManager volunteerManager;
    private ShelterManager shelterManager;
    private AccountManager accountManager;

    private HurricaneReliefApplication() {
        
        /*
        requestManager = new RequestManager(); //need to pass paarameters to the constructor
        volunteerManager = new VolunteerManager(); //need to pass parameters to the constructor
        shelterManager = new ShelterManager(); //need to pass parameters to the constructor
        */
        accountManager = new AccountManager(DataLoader.getAllUsers());
        
    }

    public static HurricaneReliefApplication getInstance() {
        return instance != null ? instance : (instance = new HurricaneReliefApplication());
    }

    public void submitHelpRequest() {

    }

    public List<Volunteer> findAvailableVolunteers() {
        return new ArrayList<>(); // Placeholder for actual implementation
    }

    public List<Shelter> findShelters() {
        return new ArrayList<>(); // Placeholder for actual implementation
    }

    public void registerUser() {

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