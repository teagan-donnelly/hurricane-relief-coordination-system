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
                          ArrayList<String> emergencyContacts, VolunteerManager volunteers) {
        super(firstName, lastName, userID, email, location, birthDate, userName, password, notificationPreference, emergencyContacts);
        this.volunteers = volunteers;
    }

    public AidCoordinator(String firstName, String lastName, String email, String userName, String password, VolunteerManager volunteers) {

        super(firstName, lastName, email, userName, password);
        this.volunteers = volunteers;
    }

    public void setPriority() {
        //TODO: Implement automatic priority calculation algorithm
    }

    public boolean verifyCertification(Volunteer volunteer, boolean approved) {
        if (volunteer == null) {
            return false;
        }

        //aid coordinator would verify the certification of the volunteer manually on UI
        return approved;
    }

    public boolean runBackgroundCheck(Volunteer volunteer, boolean passed) {
        if (volunteer == null) {
            return false;
        }

        //aid coordinator would review the background check manually on UI
        return passed;
    }

    public void setUrgency(HelpRequest request, Urgency urgency) {
        if (request == null || urgency == null) {
            throw new IllegalArgumentException("Request and urgency cannot be null.");
        }
        //urgency is manually set by the aid coordinator on their end of the UI
        request.setUrgency(urgency);
    }

    public void assignVolunteer(Volunteer volunteer, HelpRequest request) {
        //TODO: Implement volunteer assignment logic
    }

    public VolunteerManager getVolunteers() {
        return volunteers;
    }
}