package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class AidRequester extends User {

    private int householdSize;
    private AssistanceType assistanceType;
    private ArrayList<String> pastRequests;

    public AidRequester (String firstName, String lastName, UUID userID, String email, 
                Location location, LocalDate birthDate, String userName, String password,
                NotificationType notificationPreference, ArrayList<String> emergencyContacts, 
            int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        super(firstName, lastName, userID, email, location, birthDate, userName, password, 
            notificationPreference, emergencyContacts);

        this.householdSize = householdSize;
        this.assistanceType = assistanceType;
        this.pastRequests = pastRequests;
    
    }

    public int getHouseholdSize() {
        return householdSize;
    }

    public AssistanceType getAssistanceType() {
        return assistanceType;
    }

    public ArrayList<String> getPastRequests() {
        return pastRequests;
    }
}