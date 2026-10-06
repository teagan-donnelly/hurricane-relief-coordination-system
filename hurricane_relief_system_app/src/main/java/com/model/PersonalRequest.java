package com.model;

import java.util.UUID;

public class PersonalRequest extends HelpRequest {

    private int householdSize;
    private String accessInstructions;

    public PersonalRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo, int householdSize, String accessInstructions) {
        
        super(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo);

        this.householdSize = householdSize;
        this.accessInstructions = accessInstructions;

    }

    public PersonalRequest(UUID requestID, User user, AssistanceType assistanceType, Urgency urgency, 
        RequestStatus requestStatus, Location location, int householdSize, String accessInstructions) {

        super(requestID, user, assistanceType, urgency, requestStatus, location);

        this.householdSize = householdSize;
        this.accessInstructions = accessInstructions;
    }

    public int getHouseholdSize() {
        return householdSize;
    }

    public String getAccessInstructions() {
        return accessInstructions;
    }

}