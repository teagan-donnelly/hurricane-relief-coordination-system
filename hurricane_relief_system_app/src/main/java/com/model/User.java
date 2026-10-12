package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Teagan Donnelly
 */
public abstract class User {

    private UUID userID;
    private String firstName;
    private String lastName;
    private String email;
    private Location location;
    private LocalDate birthDate;
    private String userName;
    private String password;
    private NotificationType notificationPreference;
    private ArrayList<String> emergencyContacts = new ArrayList<>();

    public User(String firstName, String lastName, UUID userID,
                String email, Location location, LocalDate birthDate,
                String userName, String password,
                NotificationType notificationPreference,
                ArrayList<String> emergencyContacts) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.email = email;
        this.location = location;
        this.birthDate = birthDate;
        this.userName = userName;
        this.password = password;
        this.notificationPreference = notificationPreference;
        this.emergencyContacts = emergencyContacts;
    }

    public User(String firstName, String lastName,
                UUID userID, String password) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.password = password;
    }

    public static void makeAccount(User type, String firstName, String lastName,
                        UUID userID, String email, Location location,
                        LocalDate birthDate, boolean availability, double travelDistance,
                        ArrayList<String> skills, ArrayList<Credential> credentials, ArrayList<String> supplies,
                        boolean isVerified, boolean isDispached, double volunteerHours,
                        int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        switch (type.getClass().getSimpleName()) {
            case "Volunteer":
                new Volunteer(firstName, lastName, userID, email, location, birthDate, "", "", NotificationType.EMAIL, new ArrayList<String>(),
                    availability, travelDistance, skills, credentials, supplies, isVerified, isDispached, volunteerHours);
                break;
            case "AidReqester":
                new AidRequester(firstName, lastName, userID, email, location, birthDate, "", "", NotificationType.EMAIL, new ArrayList<String>(),
                    householdSize, assistanceType, pastRequests);
                break;
            case "AidCoordinator":
                System.out.println("Can not create an AidCoordinator account at this time.");
                //new AidCoordinator(firstName, lastName, userID, email, location, birthDate, "", "", NotificationType.EMAIL, new ArrayList<String>());
                break;
            default:
                System.out.println("Invalid user type. Account creation failed.");
                break;
        }
    }

    public UUID getUserId() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Location getLocation() {
        return location;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public NotificationType getNotificationPreference() {
        return notificationPreference;
    }

    public ArrayList<String> getEmergencyContacts() {
        return emergencyContacts;
    }
}
