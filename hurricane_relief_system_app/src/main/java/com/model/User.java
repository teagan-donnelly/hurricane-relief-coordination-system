package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Teagan Donnelly
 * Base class for every user in the system.
 */
public abstract class User {

    private UUID userID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Location location;
    private LocalDate birthDate;
    private String userName;
    private String password;
    private NotificationType notificationPreference;
    private ArrayList<String> emergencyContacts = new ArrayList<>();

    /**
     * Constructor for a brand new user. A random ID is made for them.
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param phone Phone number
     * @param userName Username
     * @param password Password
     */
    public User(String firstName, String lastName, 
                String email, String phone, String userName, String password) {

        this.userID = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Constructor for a user loaded from saved data.
     * @param firstName First name
     * @param lastName Last name
     * @param userID ID of the user
     * @param email Email address
     * @param phone Phone number
     * @param userName Username
     * @param password Password
     * @param location Location of the user
     * @param birthDate Birth date
     * @param notificationPreference How the user wants to be notified
     * @param emergencyContacts List of emergency contacts
     */
    public User(String firstName, String lastName, UUID userID,
                String email, String phone, String userName, String password,
                Location location, LocalDate birthDate,
                NotificationType notificationPreference,
                ArrayList<String> emergencyContacts) {

        this(firstName, lastName, email, phone, userName, password);

        this.userID = userID;
        this.location = location;
        this.birthDate = birthDate;
        this.notificationPreference = notificationPreference;
        this.emergencyContacts = emergencyContacts;
    }

    /**
     * Makes a new account based on the type of user.
     * @param type Type of user to make
     * @param firstName First name
     * @param lastName Last name
     * @param userID ID of the user
     * @param email Email address
     * @param phone Phone number
     * @param location Location of the user
     * @param birthDate Birth date
     * @param availability true if the volunteer is available
     * @param travelDistance How far the volunteer can travel
     * @param skills List of volunteer skills
     * @param credentials List of volunteer credentials
     * @param supplies List of volunteer supplies
     * @param isVerified true if the volunteer is verified
     * @param isDispached true if the volunteer is dispatched
     * @param volunteerHours Number of volunteer hours
     * @param householdSize Number of people in the requester's household
     * @param assistanceType Type of assistance the requester needs
     * @param pastRequests List of the requester's past requests
     */
    public static void makeAccount(User type, String firstName, String lastName,
                        UUID userID, String email, String phone, Location location,
                        LocalDate birthDate, boolean availability, double travelDistance,
                        ArrayList<String> skills, ArrayList<Credential> credentials, ArrayList<String> supplies,
                        boolean isVerified, boolean isDispached, double volunteerHours,
                        int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        switch (type.getClass().getSimpleName()) {
            case "Volunteer":
                new Volunteer(firstName, lastName, userID, email, phone, location, birthDate, "", "", NotificationType.EMAIL, new ArrayList<String>(),
                    availability, travelDistance, skills, credentials, supplies, isVerified, isDispached, volunteerHours);
                break;
            case "AidRequester":
                new AidRequester(firstName, lastName, userID, email, phone, location, birthDate, "", "", NotificationType.EMAIL, new ArrayList<String>(),
                    householdSize, assistanceType, pastRequests);
                break;
            case "AidCoordinator":
                System.out.println("Can not create an AidCoordinator account at this time.");
                //new AidCoordinator(firstName, lastName, userID, email, phone, location, birthDate, "", "", NotificationType.EMAIL, new ArrayList<String>());
                break;
            default:
                System.out.println("Invalid user type. Account creation failed.");
                break;
        }
    }

    /**
     * Gets the ID of the user.
     * @return ID of the user
     */
    public UUID getUserId() {
        return userID;
    }

    /**
     * Gets the first name.
     * @return First name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name.
     * @return Last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the email address.
     * @return Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the phone number.
     * @return Phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Gets the location of the user.
     * @return Location of the user
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the birth date.
     * @return Birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Gets the username.
     * @return Username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the password.
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets how the user wants to be notified.
     * @return Notification preference
     */
    public NotificationType getNotificationPreference() {
        return notificationPreference;
    }

    /**
     * Gets the emergency contacts.
     * @return List of emergency contacts
     */
    public ArrayList<String> getEmergencyContacts() {
        return emergencyContacts;
    }
}
