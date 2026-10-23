package com.model;

import java.util.UUID;

/**
 * Represents a request for personal assistance in the hurricane relief system.
 * This class extends the HelpRequest class and includes additional attributes specific to personal requests.
 * @author APR
 */
public class PersonalRequest extends HelpRequest {

    private int householdSize;
    private String accessInstructions;
    private int pets;
    private int children;

    /**
     * Constructor for the PersonalRequest class.
     * @param requestID the unique identifier for the personal request
     * @param requestType the type of the help request
     * @param user the user who submitted the request
     * @param comment a comment describing the help needed
     * @param assistanceType the type of assistance required
     * @param urgency the urgency level of the request
     * @param requestStatus the current status of the request
     * @param location the location where help is needed
     * @param photo an optional photo related to the request
     * @param householdSize the number of people in the household
     * @param accessInstructions instructions for accessing the location
     * @param pets the number of pets in the household
     * @param children the number of children in the household
     */
    public PersonalRequest(UUID requestID, RequestType requestType, User user, String comment, 
        AssistanceType assistanceType, Urgency urgency, RequestStatus requestStatus, 
        Location location, String photo, int householdSize, String accessInstructions, int pets, int children) {
        super(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo);

        this.householdSize = householdSize;
        this.accessInstructions = accessInstructions;
        this.pets = pets;
        this.children = children;

    }

    /**
     * Constructor for the PersonalRequest class without requestType and photo parameters.
     * @param requestID the unique identifier for the personal request
     * @param user the user who submitted the request
     * @param assistanceType the type of assistance required
     * @param urgency the urgency level of the request
     * @param requestStatus the current status of the request
     * @param location the location where help is needed
     * @param householdSize the number of people in the household
     * @param accessInstructions instructions for accessing the location
     * @param pets the number of pets in the household
     * @param children the number of children in the household
     */
    public PersonalRequest(UUID requestID, User user, AssistanceType assistanceType, Urgency urgency, 
        RequestStatus requestStatus, Location location, int householdSize, String accessInstructions, int pets, int children) {
        super(requestID, user, assistanceType, urgency, requestStatus, location);

        this.householdSize = householdSize;
        this.accessInstructions = accessInstructions;
        this.pets = pets;
        this.children = children;
    }

    /**
     * Gets the household size associated with the personal request.
     * @return the household size
     */
    public int getHouseholdSize() {
        return householdSize;
    }

    /**
     * Gets the access instructions for the personal request.
     * @return the access instructions
     */
    public String getAccessInstructions() {
        return accessInstructions;
    }

    /**
     * Gets the number of pets in the household.
     * @return the number of pets
     */
    public int getPets() {
        return pets;
    }

    /**
     * Gets the number of children in the household.
     * @return the number of children
     */
    public int getChildren() {
        return children;
    }

}