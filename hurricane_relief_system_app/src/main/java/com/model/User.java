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

    public User(String firstName, String lastName,
                String email, String userName, String password) {

        this.userID = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    public User(String firstName, String lastName,
                String email, String userName, String password,
                Location location, LocalDate birthDate,
                NotificationType notificationPreference,
                ArrayList<String> emergencyContacts) {

        this(firstName, lastName, email, userName, password);

        this.location = location;
        this.birthDate = birthDate;
        this.notificationPreference = notificationPreference;
        this.emergencyContacts = emergencyContacts;
    }

    public void makeAccount(User type, String firstName, String lastName,
                        UUID userID, String email, String address,
                        LocalDate birthDate) {

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
