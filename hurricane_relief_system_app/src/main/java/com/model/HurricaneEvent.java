package com.model;
import java.util.ArrayList;
import java.util.UUID;
public class HurricaneEvent {
    private UUID hurricanID;
    private String name;
    private int category;
    private HurricaneStatus status;
    private ArrayList<String> affectedZipCodes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;

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
