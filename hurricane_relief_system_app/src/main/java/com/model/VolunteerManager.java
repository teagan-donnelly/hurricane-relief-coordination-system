package com.model;

import java.util.ArrayList;

public class VolunteerManager {

    private static VolunteerManager instance;
    private ArrayList<Volunteer> volunteers;

    private VolunteerManager() {
        volunteers = new ArrayList<>();
    }

    public static VolunteerManager getInstance() {
    }
    
    public void addVolunteer(Volunteer volunteer) {

    }

    public void removeVolunteer(Volunteer volunteer) {
        
    }

    public void assignVolunteer(Volunteer volunteer, HelpRequest request) {

}
}
