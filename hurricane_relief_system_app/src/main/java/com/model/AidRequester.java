package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A user who asks the system for help after a hurricane.
 */
public class AidRequester extends User {

    private int householdSize;
    private AssistanceType assistanceType;
    private ArrayList<String> pastRequests;

    /**
     * Creates an aid requester.
     * @param firstName the requester's first name
     * @param lastName the requester's last name
     * @param userID the requester's unique ID
     * @param email the requester's email address
     * @param phone the requester's phone number
     * @param location where the requester lives
     * @param birthDate the requester's date of birth
     * @param userName the requester's login name
     * @param password the requester's password
     * @param notificationPreference how the requester wants to be notified
     * @param emergencyContacts the requester's emergency contacts
     * @param householdSize the number of people in the household
     * @param assistanceType the kind of help the requester usually needs
     * @param pastRequests the requester's past requests
     */
    public AidRequester (String firstName, String lastName, UUID userID, String email, String phone,
                Location location, LocalDate birthDate, String userName, String password,
                NotificationType notificationPreference, ArrayList<String> emergencyContacts, 
            int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        super(firstName, lastName, userID, email, phone, userName, password, location, birthDate, notificationPreference, emergencyContacts);
        this.householdSize = householdSize;
        this.assistanceType = assistanceType;
        this.pastRequests = pastRequests;
    
    }

    /**
     * Gets the number of people in the household.
     * @return the household size
     */
    public int getHouseholdSize() {
        return householdSize;
    }

    /**
     * Gets the kind of help the requester needs.
     * @return the assistance type
     */
    public AssistanceType getAssistanceType() {
        return assistanceType;
    }

    /**
     * Gets the requester's past requests.
     * @return list of past requests
     */
    public ArrayList<String> getPastRequests() {
        return pastRequests;
    }
}