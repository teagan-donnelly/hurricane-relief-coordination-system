package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Nicolas Gauvin
 */
public class AccountManager {

    private HashMap<Integer, User> users;
    private User currentUser;

    public AccountManager(HashMap<Integer, User> users) {
        this.users = users;
    }

    public void createUser(User type, String firstName, String lastName, String email, Location location, LocalDate birthDate,

        boolean availability, double travelDistance, ArrayList<String> skills, ArrayList<Credential> credentials,  
        ArrayList<String> supplies, boolean isVerified, boolean isDispached, double volunteerHours,

        int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        UUID userID = UUID.randomUUID();
        User.makeAccount(type, firstName, lastName, userID, email, location, birthDate, 
            availability, travelDistance, skills, credentials, supplies, isVerified, isDispached, volunteerHours,
            householdSize, assistanceType, pastRequests);
    }

    public void addUser(User user) {
    
    }

    public void deleteUser(User user) {

    }

    public void updateUser(User user) {

    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }

    public boolean login(String username, String password) {
        ArrayList<User> users = DataLoader.getAllUsers();

        for (User user : users) {
            if(user.getUserName().equals(username) && user.getPassword().equals(password)) {
                System.out.println("Login successful.");
                currentUser = user;
                return true;
            }
        }
        return false;

    }

    public void logout() {
        this.currentUser = null;
    }

}