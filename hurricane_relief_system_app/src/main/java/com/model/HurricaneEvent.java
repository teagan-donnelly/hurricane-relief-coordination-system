package com.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A class representing a hurricane event.
 * @author APR
 */

public class HurricaneEvent {

    private UUID hurricaneID;
    private String name;
    private int category;
    private HurricaneStatus status;
    private ArrayList<String> affectedZipCodes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;

    /**
     * Constructor for the HurricaneEvent class.
     * @param hurricanID the unique identifier for the hurricane event
     * @param name the name of the hurricane event
     * @param category the category of the hurricane event
     * @param status the current status of the hurricane event
     * @param affectedZipCodes a list of zip codes affected by the hurricane event
     * @param startDate the start date and time of the hurricane event
     * @param endDate the end date and time of the hurricane event
     * @param description a description of the hurricane event
     */

    public HurricaneEvent(UUID hurricanID, String name, int category, HurricaneStatus status, ArrayList<String> affectedZipCodes, LocalDateTime startDate, LocalDateTime endDate, String description) {
        this.hurricaneID = hurricanID;
        this.name = name;
        this.category = category;
        this.status = status;
        this.affectedZipCodes = affectedZipCodes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    /**
     * Ends the hurricane event by setting the end date and time.
     * @param endDate the end date and time of the hurricane event
     */
    
    public void endHurricantEvent(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Adds a zip code to the list of affected zip codes for the hurricane event.
     * @param zipCode the zip code to be added to the list of affected zip codes
     */

    public void addAffectedZipCode(String zipCode) {
        affectedZipCodes.add(zipCode);
    }

    /**
     * Removes a zip code from the list of affected zip codes for the hurricane event.
     * @param zipCode the zip code to be removed from the list of affected zip codes
     */

    public void removeAffectedZipCode(String zipCode) {
        affectedZipCodes.remove(zipCode);
    }

    /**
     * Updates the category of the hurricane event and notifies users of the change.
     * @param newCategory the new category for the hurricane event
     */

    public void updateCategory(int newCategory) {
        notifyUsers("The Category of the Hurricane has changed from " + this.category + " to " + newCategory);
        this.category = newCategory;
    }

    /**
     * Updates the status of the hurricane event and notifies users of the change.
     * @param newStatus the new status for the hurricane event
     */

    public void updateStatus(HurricaneStatus newStatus) {
        notifyUsers("The Status of the Hurricane has changed from " + this.status + " to " + newStatus);
        this.status = newStatus;
    }

    /**
     * Notifies all users of the hurricane event with a given message.
     * @param message the message to be sent to all users
     */

    public void notifyUsers(String message) {
        ArrayList<User> users = DataLoader.getAllUsers();

        for (User user : users) {
            NotificationType notification = user.getNotificationPreference();

            switch (notification) {
                case SMS:
                    System.out.println("A SMS message was sent to: "+ user.getUserName());
                    break;

                case EMAIL:
                    System.out.println("An EMAIL message was sent to: "+ user.getUserName());
                    break;

                case APP_PUSH:
                    System.out.println("An APP PUSH message was sent to: "+ user.getUserName());
                    break;
            
                default:
                    System.out.println("Could not notify user: "+ user.getUserName());
                    break;
            }
        }
    }

    //getters
    /**
     * Gets the unique identifier of the hurricane event.
     * @return the unique identifier of the hurricane event
     */
    public UUID getHuricaneID() {
        return hurricaneID;
    }

    /**
     * Gets the name of the hurricane event.
     * @return the name of the hurricane event
     */
    public String getName() {
        return name;
    }
}