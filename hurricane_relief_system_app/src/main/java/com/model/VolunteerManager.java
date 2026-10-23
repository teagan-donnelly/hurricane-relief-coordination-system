package com.model;

import java.util.ArrayList;

/**
 * @author Nicolas Gauvin
 * Manages the collection of volunteers in the hurricane relief system.
 */
public class VolunteerManager {
    private static VolunteerManager instance;
    private ArrayList<Volunteer> volunteers;

    /**
     * Private constructor that loads the volunteers from the json file.
     */
    private VolunteerManager() {
        volunteers = new ArrayList<>();
        ArrayList<User> users = DataLoader.getUsers(AppConstants.VOLUNTEER_JSON);

        for (User user : users) {
            if (user instanceof Volunteer) {
                volunteers.add((Volunteer) user);
            }
        }
    }

    /**
     * Gets the shared VolunteerManager, creating it on first use.
     * @return the single VolunteerManager instance
     */
    public static VolunteerManager getInstance() {
        if (instance == null) {
            instance = new VolunteerManager();
        }
        return instance;
    }

    /**
     * Adds a volunteer to the manager.
     * @param volunteer Volunteer to add
     */
    public void addVolunteer(Volunteer volunteer) {
        if (volunteer != null && !volunteers.contains(volunteer)) {
            volunteers.add(volunteer);
        }
    }

    /**
     * Removes a volunteer from the manager.
     * @param volunteer Volunteer to remove
     */
    public void removeVolunteer(Volunteer volunteer) {
        if (volunteer != null) {
            volunteers.remove(volunteer);
        }
    }

    /**
     * Assigns a volunteer to a help request.
     * @param volunteer Volunteer to assign
     * @param request Help request to assign them to
     */
    public void assignVolunteer(Volunteer volunteer, HelpRequest request) {
        if (volunteer == null || request == null) {
            System.out.println("Volunteer and request cannot be null.");
            return;
        }

        if (!volunteer.isAvailability() || volunteer.isDispatched()) {
            System.out.println("Volunteer is not available for this request.");
            return;
        }

        if (request.getRequestStatus() == RequestStatus.CANCELED ||
            request.getRequestStatus() == RequestStatus.COMPLETED) {
            System.out.println("Cannot assign a volunteer to a canceled or completed request.");
            return;
        }

        volunteer.setDispatched(true);
        request.setRequestStatus(RequestStatus.ASSIGNED);
        request.setAssignedVolunteerID(volunteer.getUserId());
        System.out.println("Volunteer " + volunteer.getFirstName() + " was assigned to the request.");
    }

    /**
     * Gets the list of volunteers.
     * @return ArrayList of volunteers
     */
    public ArrayList<Volunteer> getVolunteers() {
        return volunteers;
    }
}