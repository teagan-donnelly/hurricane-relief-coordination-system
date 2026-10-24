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
public class Coordinator extends User {

    private VolunteerManager volunteers;
    private CoordinatorRole role;

    /**
     * Constructor for a coordinator loaded from saved data.
     * @param role Role of the coordinator
     * @param firstName First name
     * @param lastName Last name
     * @param userID ID of the user
     * @param email Email address
     * @param phone Phone number
     * @param userName Username
     * @param password Password
     * @param location Location of the coordinator
     * @param birthDate Birth date
     * @param notificationPreference How the coordinator wants to be notified
     * @param emergencyContacts List of emergency contacts
     * @param volunteers Volunteer manager the coordinator uses
     */
    public Coordinator(CoordinatorRole role, String firstName, String lastName, UUID userID, String email, String phone, String userName, String password,
                          Location location, LocalDate birthDate, NotificationType notificationPreference, ArrayList<String> emergencyContacts,
                          VolunteerManager volunteers) {
        super(firstName, lastName, userID, email, phone, userName, password, location, birthDate, notificationPreference, emergencyContacts);
        this.role = role;
        this.volunteers = volunteers;
    }

    /**
     * Constructor for a brand new coordinator.
     * @param role Role of the coordinator
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param phone Phone number
     * @param userName Username
     * @param password Password
     * @param volunteers Volunteer manager the coordinator uses
     */
    public Coordinator(CoordinatorRole role, String firstName, String lastName, String email, String phone, String userName, String password, VolunteerManager volunteers) {

        super(firstName, lastName, email, phone, userName, password);
        this.role = role;
        this.volunteers = volunteers;
    }


    /**
     * Verifies a volunteer's certification.
     * @param volunteer Volunteer being verified
     * @param approved true if the coordinator approved the certification
     * @return true if the certification is approved, false otherwise
     */
    public boolean verifyCertification(Volunteer volunteer, boolean approved) {
        if (volunteer == null) {
            return false;
        }

        //aid coordinator would verify the certification of the volunteer manually on UI
        return approved;
    }

    /**
     * Runs a background check on a volunteer.
     * @param volunteer Volunteer being checked
     * @param passed true if the volunteer passed the background check
     * @return true if the volunteer passed, false otherwise
     */
    public boolean runBackgroundCheck(Volunteer volunteer, boolean passed) {
        if (volunteer == null) {
            return false;
        }

        //aid coordinator would review the background check manually on UI
        return passed;
    }

    /**
     * Sets the urgency of a help request.
     * @param request Help request to update
     * @param urgency New urgency of the request
     */
    public void setUrgency(HelpRequest request, Urgency urgency) {
        if (request == null || urgency == null) {
            throw new IllegalArgumentException("Request and urgency cannot be null.");
        }
        //urgency is manually set by the aid coordinator on their end of the UI
        request.setUrgency(urgency);
    }

    /**
     * Assigns a volunteer to a help request if they are available and qualified.
     * @param volunteer Volunteer being assigned
     * @param request Help request the volunteer is assigned to
     */
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

    /**
     * Finds the closest qualified volunteer and assigns them to the request.
     * @param request Help request that needs a volunteer
     * @return Volunteer that was assigned, or null if none was found
     * @throws IOException if the map lookup fails
     * @throws InterruptedException if the map lookup is interrupted
     */
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

    /**
     * Converts a location into an address for the map tools.
     * @param location Location to convert
     * @return Address of the location
     */
    private Address toAddress(Location location) {
        Address address = new Address(
            location.getStreetAddress(),
            location.getCity(),
            location.getState(),
            location.getZipcode()
        );

        return address;
    }

    /**
     * Checks if a volunteer is qualified for a type of assistance.
     * @param volunteer Volunteer being checked
     * @param type Type of assistance needed
     * @return true if the volunteer is qualified, false otherwise
     */
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

    /**
     * Checks if a volunteer has a valid credential of the given type.
     * @param volunteer Volunteer being checked
     * @param type Type of credential needed
     * @return true if the volunteer has the credential, false otherwise
     */
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

    /**
     * Checks if a volunteer has a skill.
     * @param volunteer Volunteer being checked
     * @param requiredSkill Skill needed
     * @return true if the volunteer has the skill, false otherwise
     */
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

    /**
     * Gets the volunteer manager.
     * @return Volunteer manager
     */
    public VolunteerManager getVolunteers() {
        return volunteers;
    }

    /**
     * Gets the role of the coordinator.
     * @return Role of the coordinator
     */
    public CoordinatorRole getRole() {
        return role;
    }
}
