package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Nicolas Gauvin
 */
public class AccountManager {

    private ArrayList<User> users;
    private User currentUser;

    public AccountManager(ArrayList<User> users) {
        this.users = users;
    }

    public static void createUser(User type, String firstName, String lastName, String email, Location location, LocalDate birthDate,

        boolean availability, double travelDistance, ArrayList<String> skills, ArrayList<Credential> credentials,  
        ArrayList<String> supplies, boolean isVerified, boolean isDispached, double volunteerHours,

        int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        UUID userID = UUID.randomUUID();
        User.makeAccount(type, firstName, lastName, userID, email, type.getPhone(), location, birthDate,
            availability, travelDistance, skills, credentials, supplies, isVerified, isDispached, volunteerHours,
            householdSize, assistanceType, pastRequests);
    }

    public void addUser(User user) {
        users.add(user);
    
    }

    public void deleteUser(User user) {
        users.remove(user);

    }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                return;
            }
        }

    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public boolean login(String username, String password) {
        for (User user : users) {
            if(user.getUserName().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;

    }

    public boolean logout() {
        if (currentUser != null) {
            this.currentUser = null;
            return true;
        } else {
            return false;
        }
    }

}
