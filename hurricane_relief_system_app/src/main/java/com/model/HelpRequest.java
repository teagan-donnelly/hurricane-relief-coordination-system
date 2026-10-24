package com.model;

import java.util.UUID;

/**
 * Represents a help request made by a user in the hurricane relief system.
 * @author APR & Teagan Donnelly
 */
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
    private UUID assignedVolunteerID;
    private UUID selectedShelterID;

    public UUID getSelectedShelterID() {
        return selectedShelterID;
    }

    public void setSelectedShelterID(UUID selectedShelterID) {
        this.selectedShelterID = selectedShelterID;
    }

    /**
     * Constructor for the HelpRequest class.
     * @param requestID the unique identifier for the help request
     * @param requestType the type of the help request
     * @param user the user who submitted the request
     * @param comment a comment describing the help needed
     * @param assistanceType the type of assistance required
     * @param urgency the urgency level of the request
     * @param requestStatus the current status of the request
     * @param location the location where help is needed
     * @param photo an optional photo related to the request
     */
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

    /**
     * Constructor for the HelpRequest class without requestType and photo parameters.
     * @param requestID the unique identifier for the help request
     * @param user the user who submitted the request
     * @param assistanceType the type of assistance required
     * @param urgency the urgency level of the request
     * @param requestStatus the current status of the request
     * @param location the location where help is needed
     */
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

    /**
     * Updates the details of the help request.
     * @param comment a comment describing the help needed
     * @param assistanceType the type of assistance required
     * @param location the location where help is needed
     * @param photo an optional photo related to the request
     */
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

    /**
     * Cancels the help request.
     */
    public void cancelRequest() {

        if (requestStatus == RequestStatus.COMPLETED) {
            throw new IllegalStateException(
                "Cannot cancel a completed request."
            );
        }

        this.requestStatus = RequestStatus.CANCELED;
    }

    /**
     * Marks the request as completed. Only a shelter manager is allowed to do this.
     * @param user the user trying to complete the request
     * @return true if the request was completed, false otherwise
     */
    public boolean markComplete(User user) {
        if (!(user instanceof AidCoordinator) ||
            ((AidCoordinator) user).getRole() != CoordinatorRole.SHELTER) {
            System.out.println("Permission denied: only a shelter manager can mark a request as complete.");
            return false;
        }

        if (requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.COMPLETED) {
            System.out.println("This request is already " + requestStatus + ".");
            return false;
        }

        requestStatus = RequestStatus.COMPLETED;
        return true;
    }

    /**
     * Tracks the status of the help request.
     * @return the current status of the request
     */
    public RequestStatus trackRequestStatus() {
        return requestStatus;
    }

    /**
     * Gets the unique identifier of the help request.
     * @return the unique identifier of the request
     */
    public UUID getRequestID() {
        return requestID;
    }

    /**
     * Gets the status of the help request.
     * @return the status of the request
     */
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    /**
     * Gets the urgency level of the help request.
     * @return the urgency level of the request
     */
    public Urgency getUrgency() {
        return urgency;
    }

    /**
     * Gets the type of the help request.
     * @return the type of the help request
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Gets the user who submitted the help request.
     * @return the user who submitted the request
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the comment describing the help needed for the request.
     * @return the comment describing the help needed
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the location where help is needed for the request.
     * @return the location where help is needed
     */
    public AssistanceType getAssistanceType() {
        return assistanceType;
    }

    /**
     * Gets the location where help is needed for the request.
     * @return the location where help is needed
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the photo related to the help request.
     * @return the photo related to the request
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Sets the status of the help request.
     * @param status the new status of the request
     */
    public void setRequestStatus(RequestStatus status) {
        this.requestStatus = status;
    }

    /**
     * Sets the urgency level of the help request.
     * @param urgency the urgency level of the request
     */
    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    /**
     * Gets the ID of the volunteer handling this request.
     * @return the volunteer's ID, or null if no one is assigned
     */
    public UUID getAssignedVolunteerID() {
        return assignedVolunteerID;
    }

    /**
     * Sets the volunteer handling this request.
     * @param assignedVolunteerID the volunteer's ID
     */
    public void setAssignedVolunteerID(UUID assignedVolunteerID) {
        this.assignedVolunteerID = assignedVolunteerID;
    }
}