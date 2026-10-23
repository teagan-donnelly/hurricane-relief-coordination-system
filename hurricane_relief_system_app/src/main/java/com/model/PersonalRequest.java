package com.model;

import java.util.UUID;

public class PersonalRequest extends HelpRequest {

    private int householdSize;
    private String accessInstructions;
    private int pets;
    private int children;

    public PersonalRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo, int householdSize, String accessInstructions, int pets, int children) {
        super(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo);

        this.householdSize = householdSize;
        this.accessInstructions = accessInstructions;
        this.pets = pets;
        this.children = children;

    }

    public PersonalRequest(UUID requestID, User user, AssistanceType assistanceType, Urgency urgency, 
        RequestStatus requestStatus, Location location, int householdSize, String accessInstructions, int pets, int children) {
        super(requestID, user, assistanceType, urgency, requestStatus, location);

        this.householdSize = householdSize;
        this.accessInstructions = accessInstructions;
        this.pets = pets;
        this.children = children;
    }

    public int getHouseholdSize() {
        return householdSize;
    }

    public String getAccessInstructions() {
        return accessInstructions;
    }

    public int getPets() {
        return pets;
    }

    public int getChildren() {
        return children;
    }

}