package com.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class HurricaneEvent {

    private UUID hurricaneID;
    private String name;
    private int category;
    private HurricaneStatus status;
    private ArrayList<String> affectedZipCodes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;

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
    
    public void endHurricantEvent(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void addAffectedZipCode(String zipCode) {
        affectedZipCodes.add(zipCode);
    }

    public void removeAffectedZipCode(String zipCode) {
        affectedZipCodes.remove(zipCode);
    }

    public void updateCategory(int newCategory) {
        notifyUsers("The Category of the Hurricane has changed from " + this.category + " to " + newCategory);
        this.category = newCategory;
    }

    public void updateStatus(HurricaneStatus newStatus) {
        notifyUsers("The Status of the Hurricane has changed from " + this.status + " to " + newStatus);
        this.status = newStatus;
    }

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
}