package com.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a request for infrastructure assistance in the hurricane relief system.
 * This class extends the HelpRequest class and includes additional attributes specific to infrastructure requests.
 * @author APR
 */
public class InfrustructureRequest extends HelpRequest {

    private HazardType hazardType;
    private LocalDateTime reportTime;

    /**
     * Constructor for the InfrustructureRequest class.
     * @param requestID the unique identifier for the infrastructure request
     * @param requestType the type of the help request
     * @param user the user who submitted the request
     * @param comment a comment describing the help needed
     * @param assistanceType the type of assistance required
     * @param urgency the urgency level of the request
     * @param requestStatus the current status of the request
     * @param location the location where help is needed
     * @param photo an optional photo related to the request
     * @param hazardType the type of hazard associated with the infrastructure request
     * @param reportTime the date and time when the hazard was reported
     */
    public InfrustructureRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo, HazardType hazardType, LocalDateTime reportTime) {

        super(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo);
        
        this.hazardType = hazardType;
        this.reportTime = reportTime;
    }

    /**
     * Constructor for the InfrustructureRequest class without requestType and photo parameters.
     * @param requestID the unique identifier for the infrastructure request
     * @param user the user who submitted the request
     * @param assistanceType the type of assistance required
     * @param urgency the urgency level of the request
     * @param requestStatus the current status of the request
     * @param location the location where help is needed
     * @param hazardType the type of hazard associated with the infrastructure request
     * @param reportTime the date and time when the hazard was reported
     */
    public InfrustructureRequest(UUID requestID, User user, AssistanceType assistanceType, Urgency urgency, 
        RequestStatus requestStatus, Location location, HazardType hazardType, LocalDateTime reportTime) {

        super(requestID, user, assistanceType, urgency, requestStatus, location);

        this.hazardType = hazardType;
        this.reportTime = reportTime;
    }

    /**
     * Gets the hazard type associated with the infrastructure request.
     * @return the hazard type
     */
    public HazardType getHazardType() {
        return hazardType;
    }

    /**
     * Gets the report time of the infrastructure request.
     * @return the report time
     */
    public LocalDateTime getReportTime() {
        return reportTime;
    }
}