package com.model;

import java.util.UUID;

public class HelpRequest {

    private UUID requestID;
    private RequestType requestType;
    private User user;
    private String comment;
    private AssistanceType assistanceType;
    private Urgency urgency;
    private RequestStatus requestStatus;
    private Location location;
    private String photo;

    public HelpRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo) {
        
        this.requestID = requestID;
        this.requestType = requestType;
        this.user = user;
        this.comment = comment;
        this.assistanceType = assistanceType;
        this.urgency = urgency;
        this.requestStatus = requestStatus;
        this.location = location;
        this.photo = photo;
    }

    public HelpRequest(UUID requestID, User user, AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, Location location) {
        this.requestID = requestID; 
        this.user = user; 
        this.assistanceType = assistanceType;
        this.urgency = urgency;
        this.requestStatus = requestStatus;
        this.location = location;
    }

    public boolean isUrgent(Urgency urgency) {
        return true;
    }

    public void updateRequest() {

    }

    public void cancelRequest() {

    }

    public void trackRequestStatus() {

    }

}