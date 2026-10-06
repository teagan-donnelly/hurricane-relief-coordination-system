package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Nicolas Gauvin
 * AidCoordinator
 */
public class AidCoordinator extends User {

    private VolunteerManager volunteers;

    public AidCoordinator(String firstName, String lastName, UUID userID, String email, Location location,
                          LocalDate birthDate, String userName, String password, NotificationType notificationPreference,
                          ArrayList emergencyContacts, VolunteerManager volunteers) {
        super(firstName, lastName, userID, email, location, birthDate, userName, password, notificationPreference, emergencyContacts);
        this.volunteers = volunteers;
    }

    public AidCoordinator(VolunteerManager volunteers) {
        super("", "", null, "");
        this.volunteers = volunteers;
    }

    public void setPriority() {

    }

    public boolean verifyCertification() {
        return true;
    }

    public boolean runBackgroundCheck() {
        return true;
    }

    public void setUrgency(HelpRequest request, int urgency) {

    }

    public void assignVolunteer(Volunteer volunteer, HelpRequest request) {

    }

    public VolunteerManager getVolunteers() {
        return volunteers;
    }
}