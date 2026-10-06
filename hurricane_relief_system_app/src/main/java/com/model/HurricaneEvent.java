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

    public HurricaneEvent(UUID hurricaneID, String name, int category, HurricaneStatus status,
        ArrayList<String> affectedZipCodes, LocalDateTime startDate, LocalDateTime endDate, String description) {

        this.hurricaneID = hurricaneID;
        this.name = name;
        this.category = category;
        this.status = status;
        this.affectedZipCodes = affectedZipCodes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    
    }

    public void endHurricantEvent(HurricaneEvent hurricaneEvent, LocalDateTime endDate) {
        hurricaneEvent.endDate = endDate;
    }

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