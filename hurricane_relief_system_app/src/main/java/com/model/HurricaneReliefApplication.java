package com.model;

import java.util.List;

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

    public RequestStatus getRequestStatus() {
        return true;
    }

    public void login() {

    }

    public void logout() {
        
    }


}