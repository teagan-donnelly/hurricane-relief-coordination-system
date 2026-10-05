package com.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class InfrustructureRequest extends HelpRequest {

    private HazardType hazardType;
    private LocalDateTime reportTime;

    public InfrustructureRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo, HazardType hazardType, LocalDateTime reportTime) {

        super(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo);
        
        this.hazardType = hazardType;
        this.reportTime = reportTime;
    }

    public InfrustructureRequest(UUID requestID, User user, AssistanceType assistanceType, Urgency urgency, 
        RequestStatus requestStatus, Location location, HazardType hazardType, LocalDateTime reportTime) {

        super(requestID, user, assistanceType, urgency, requestStatus, location);

        this.hazardType = hazardType;
        this.reportTime = reportTime;
    }
}