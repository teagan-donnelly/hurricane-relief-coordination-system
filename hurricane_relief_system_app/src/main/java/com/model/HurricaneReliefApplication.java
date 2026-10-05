package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HurricaneReliefApplication {

    private instance ApplicationFacade;
    private requestManager RequestManager;
    private volunteerManager VolunteerManager;
    private shelterManager ShelterManager;
    private accountManager AccountManager;

    private ApplicationFacade() {
        return true;
    }

    public ApplicationFacade getInstance() {
        return true;
    }

    public void submitHelpRequest() {

    }

    public List<Volunteer> findAvailableVolunteers() {
        return true;
    }

    public List<Shelter> findShelters() {
        return true;
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

    public void login() {

    }

    public void logout() {
        
    }


}