package com.model;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.UUID;
public class HurricaneEvent {
    private UUID hurricanID;
=======

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class HurricaneEvent {

    private UUID hurricaneID;
>>>>>>> Aden-workspace
    private String name;
    private int category;
    private HurricaneStatus status;
    private ArrayList<String> affectedZipCodes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;

<<<<<<< HEAD
<<<<<<< HEAD
    public HurricaneEvent(UUID hurricanID, String name, int category, HurricaneStatus status, ArrayList<String> affectedZipCodes, LocalDateTime startDate, LocalDateTime endDate, String description) {

    }
    
    
    public HurricaneEvent(UUID hurricanID, String name, int category, HurricaneStatus status, ArrayList<String> affectedZipCodes, LocalDateTime startDate, LocalDateTime endDate, String description) {
    }

    public void addAffectedZipCode(String zipCode) {
}
    public void removeAffectedZipCode(String zipCode) {
        affectedZipCodes.remove(zipCode);
    }

    public void updateCategory(int newCategory) {
        this.category = newCategory;
    }

    public void updateStatus(HurricaneStatus newStatus) {
    }

    public void notifyUser() {
        
    }











}
=======
=======
<<<<<<< Updated upstream
>>>>>>> Aden-workspace
    public HurricaneEvent(UUID hurricaneID, String name, int category, HurricaneStatus status,
        ArrayList<String> affectedZipCodes, LocalDateTime startDate, LocalDateTime endDate, String description) {

        this.hurricaneID = hurricaneID;
=======
    public HurricaneEvent(UUID hurricanID, String name, int category, HurricaneStatus status, ArrayList<String> affectedZipCodes, LocalDateTime startDate, LocalDateTime endDate, String description) {
        this.hurricaneID = hurricanID;
>>>>>>> Stashed changes
        this.name = name;
        this.category = category;
        this.status = status;
        this.affectedZipCodes = affectedZipCodes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
<<<<<<< Updated upstream
    
    }

=======
    }
    
    
>>>>>>> Stashed changes
    public void endHurricantEvent(HurricaneEvent hurricaneEvent, LocalDateTime endDate) {
        hurricaneEvent.endDate = endDate;
    }

<<<<<<< Updated upstream
    public void addAffectedZipcode(String zipcode) {

    }

    public void removeAffectedZipcode(String zipcode) {

    }

    public void updateCategory(int category) {

    }

    public void updateStatus(HurricaneStatus status) {

    }

    public void notifyUser() {

    }

}
<<<<<<< HEAD
>>>>>>> Aden-workspace
=======
=======
    public void addAffectedZipCode(String zipCode) {
}
    public void removeAffectedZipCode(String zipCode) {
        affectedZipCodes.remove(zipCode);
    }

    public void updateCategory(int newCategory) {
        this.category = newCategory;
    }

    public void updateStatus(HurricaneStatus newStatus) {
    }

    public void notifyUser() {
        
    }











}
>>>>>>> Stashed changes
>>>>>>> Aden-workspace
