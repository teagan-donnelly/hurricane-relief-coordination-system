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

    /**
     * Checks if the help request is urgent based on its urgency level.
     * @param urgency the urgency level of the help request assigned by the aid coordinator
     * @return true if the urgency level is HIGH_PRIORITY or URGENT_PRIORITY, false otherwise
     */
    public boolean isUrgent(Urgency urgency) {
        return urgency == Urgency.HIGH_PRIORITY || urgency == Urgency.URGENT_PRIORITY;
    }

    public void updateRequest(String comment, AssistanceType assistanceType,
                            Location location, String photo) {

        if (requestStatus == RequestStatus.CANCELED ||
            requestStatus == RequestStatus.COMPLETED) {
            throw new IllegalStateException(
                "Cannot edit a canceled or completed request."
            );
        }

        this.comment = comment;
        this.assistanceType = assistanceType;
        this.location = location;
        this.photo = photo;
    }

    public void cancelRequest() {

        if (requestStatus == RequestStatus.COMPLETED) {
            throw new IllegalStateException(
                "Cannot cancel a completed request."
            );
        }

        this.requestStatus = RequestStatus.CANCELED;
    }

    public RequestStatus trackRequestStatus() {
        return requestStatus;
    }

    public UUID getRequestID() {
        return requestID;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public User getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }

    public AssistanceType getAssistanceType() {
        return assistanceType;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public Location getLocation() {
        return location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setRequestStatus(RequestStatus status) {
        this.requestStatus = status;
    }
}