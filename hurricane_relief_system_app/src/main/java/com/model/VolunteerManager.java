package com.model;

import java.util.ArrayList;

/**
 * @author Nicolas Gauvin
 * Manages the collection of volunteers in the hurricane relief system.
 */
public class VolunteerManager {
    private ArrayList<Volunteer> volunteers;

    /**
     * Constructor initializing with an existing list of volunteers.
     * @param volunteers List of initial volunteers
     */
    public VolunteerManager(ArrayList<Volunteer> volunteers) {
        if (volunteers != null) {
            this.volunteers = volunteers;
        } else {
            this.volunteers = new ArrayList<>();
        }
    }

    /**
     * Default constructor initializing an empty volunteer list.
     */
    public VolunteerManager() {
        this.volunteers = new ArrayList<>();
    }

    /**
     * Adds a volunteer to the manager.
     * @param volunteer Volunteer to add
     */
    public void addVolunteer(Volunteer volunteer) {
        if (volunteer != null) {
            this.volunteers.add(volunteer);
        }
    }

    /**
     * Removes a volunteer from the manager.
     * @param volunteer Volunteer to remove
     */
    public void removeVolunteer(Volunteer volunteer) {
        if (volunteer != null) {
            this.volunteers.remove(volunteer);
        }
    }

    /**
     * Gets the list of volunteers.
     * @return ArrayList of volunteers
     */
    public ArrayList<Volunteer> getVolunteers() {
        return volunteers;
    }
}