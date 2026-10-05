package com.model;

public class AidCoordinator {

    private VolunteerManager volunteers;

    public AidCoordinator(String firstName, String lastName, UUID userID, String email, String address, LocalDate birthDate, String userName, String password, NotificationType notificatonPreference, ArrayList<String> emergencyContacts, VolunteerManager volunteers) {
        super(firstName, lastName, userID, email, address, birthDate, userName, password, notificatonPreference, emergencyContacts);
        
    }

    public void setPriority() {

    }

    public boolean verifyCertification() {

    }

    public boolean runBackgroundCheck() {
    }
    
}
