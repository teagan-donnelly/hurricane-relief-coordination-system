package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HurricaneReliefApplication {

    private static HurricaneReliefApplication instance;

    private static RequestManager requestManager;
    private static VolunteerManager volunteerManager;
    private static ShelterManager shelterManager;
    private AccountManager accountManager;

    private HurricaneReliefApplication() {
        requestManager = new RequestManager(); //need to pass paarameters to the constructor
        volunteerManager = new VolunteerManager(); //need to pass parameters to the constructor
        shelterManager = new ShelterManager(); //need to pass parameters to the constructor
        accountManager = new AccountManager(); //need to pass parameters to the constructor
    }

    public static HurricaneReliefApplication getInstance() {
        return instance != null ? instance : (instance = new HurricaneReliefApplication());
    }

    public void submitHelpRequest() {

    }

    public List<Volunteer> findAvailableVolunteers() {
        
    }

    public List<Shelter> findShelters() {
        
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
        accountManager.login(username, password);
    }

    public void logout() {
        accountManager.logout();
    }


}