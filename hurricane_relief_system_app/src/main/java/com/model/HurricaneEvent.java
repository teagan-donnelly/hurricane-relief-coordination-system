package com.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
/*
* @author Jorge Naranjo
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
        this.category = newCategory;
    }

    public void updateStatus(HurricaneStatus newStatus) {
    }

    public void notifyUser() {
        
    }
}