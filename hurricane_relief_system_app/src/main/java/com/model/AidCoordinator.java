package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import com.location.Address;
import com.location.MapTools;
import java.io.IOException;

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
        if (volunteer == null || request == null || volunteers == null) {
            System.out.println("Volunteer, request, and manager are required.");
            return;
        }

        if (request.getRequestStatus() != RequestStatus.APPROVED &&
            request.getRequestStatus() != RequestStatus.AWAITING_ASSIGNMENT) {
            System.out.println("Request is not ready for assignment.");
            return;
        }

        if (!volunteer.isAvailability() || volunteer.isDispatched() ||
            !volunteer.isVerified()) {
            System.out.println("Volunteer is not available or verified.");
            return;
        }

        if (!isQualified(volunteer, request.getAssistanceType())) {
            System.out.println("Volunteer does not have the required qualifications.");
            return;
        }

        volunteers.assignVolunteer(volunteer, request);
    }

    public Volunteer autoAssignVolunteer(HelpRequest request)
            throws IOException, InterruptedException {

        if (request == null || request.getLocation() == null ||
            request.getAssistanceType() == null) {
            System.out.println("Request must have a location and assistance type.");
            return null;
        }

        if (request.getRequestStatus() != RequestStatus.APPROVED &&
            request.getRequestStatus() != RequestStatus.AWAITING_ASSIGNMENT) {
            System.out.println("Request is not ready for assignment.");
            return null;
        }

        if (volunteers == null) {
            System.out.println("Volunteer manager is missing.");
            return null;
        }

        ArrayList<Volunteer> qualifiedVolunteers = new ArrayList<>();
        ArrayList<Address> addresses = new ArrayList<>();

        for (Volunteer volunteer : volunteers.getVolunteers()) {
            if (volunteer != null && volunteer.getLocation() != null) {
                if (volunteer.isAvailability() && volunteer.isVerified() &&
                    !volunteer.isDispatched()) {

                    if (isQualified(volunteer, request.getAssistanceType())) {
                        qualifiedVolunteers.add(volunteer);
                        addresses.add(toAddress(volunteer.getLocation()));
                    }
                }
            }
        }

        if (qualifiedVolunteers.isEmpty()) {
            System.out.println("No qualified volunteers are available.");
            return null;
        }

        Address[] volunteerAddresses = new Address[addresses.size()];

        for (int i = 0; i < addresses.size(); i++) {
            volunteerAddresses[i] = addresses.get(i);
        }

        Address requestAddress = toAddress(request.getLocation());
        Address closestAddress = MapTools.getInstance().findClosestAddress(
            requestAddress, volunteerAddresses
        );

        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i) == closestAddress) {
                Volunteer match = qualifiedVolunteers.get(i);
                assignVolunteer(match, request);
                return match;
            }
        }

        return null;
    }

    private Address toAddress(Location location) {
        Address address = new Address(
            location.getStreetAddress(),
            location.getCity(),
            location.getState(),
            location.getZipcode()
        );

        return address;
    }

    private boolean isQualified(Volunteer volunteer, AssistanceType type) {
        if (type == AssistanceType.MEDICAL || type == AssistanceType.CHILD_INJURY) {
            return hasCredential(volunteer, CredentialType.EMT) ||
                   hasCredential(volunteer, CredentialType.PARAMEDIC) ||
                   hasCredential(volunteer, CredentialType.NURSE) ||
                   hasCredential(volunteer, CredentialType.DOCTOR);
        }

        if (type == AssistanceType.RESCUE || type == AssistanceType.FLOOD) {
            return hasCredential(volunteer, CredentialType.SEARCH_AND_RESCUE) ||
                   hasCredential(volunteer, CredentialType.FIREFIGHTER);
        }

        if (type == AssistanceType.FOOD_SUPPLY) {
            return hasSkill(volunteer, "Food Distribution");
        }

        if (type == AssistanceType.SHELTER) {
            return hasSkill(volunteer, "Shelter Assistance");
        }

        if (type == AssistanceType.PROPERTY_DAMAGE) {
            return hasSkill(volunteer, "Construction") ||
                   hasSkill(volunteer, "Debris Removal");
        }

        if (type == AssistanceType.TRANSPORT) {
            return hasSkill(volunteer, "Transportation");
        }

        if (type == AssistanceType.MEDICAL_SUPPLIES) {
            return hasSkill(volunteer, "Medical Supply Delivery");
        }

        if (type == AssistanceType.PET_INJURY) {
            return hasSkill(volunteer, "Animal Care");
        }

        return false;
    }

    private boolean hasCredential(Volunteer volunteer, CredentialType type) {
        if (volunteer.getCredentials() == null) {
            return false;
        }

        for (Credential credential : volunteer.getCredentials()) {
            if (credential != null) {
                if (credential.getType() == type &&
                    "Valid".equalsIgnoreCase(credential.getStatus())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasSkill(Volunteer volunteer, String requiredSkill) {
        if (volunteer.getSkills() == null) {
            return false;
        }

        for (String skill : volunteer.getSkills()) {
            if (skill != null) {
                if (skill.trim().equalsIgnoreCase(requiredSkill)) {
                    return true;
                }
            }
        }

        return false;
    }

    public VolunteerManager getVolunteers() {
        return volunteers;
    }
}
