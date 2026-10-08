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

    public AidCoordinator(String firstName, String lastName, String email, String userName, String password, Location location, LocalDate birthDate,
                        NotificationType notificationPreference, ArrayList<String> emergencyContacts, VolunteerManager volunteers) {

        super(firstName, lastName, email, userName, password, location, birthDate, notificationPreference, emergencyContacts);

        this.volunteers = volunteers;
    }

    public AidCoordinator(String firstName, String lastName, String email, String userName, String password, VolunteerManager volunteers) {

        super(firstName, lastName, email, userName, password);
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

    public void setUrgency(HelpRequest request, Urgency urgency) {

    }

    public void assignVolunteer(Volunteer volunteer, HelpRequest request) {

    }

    public VolunteerManager getVolunteers() {
        return volunteers;
    }
}