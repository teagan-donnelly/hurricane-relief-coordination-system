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

    /**
     * Creates an account manager for a list of users.
     * @param users the users who can log in
     */
    public AccountManager(ArrayList<User> users) {
        this.users = users;
    }

    /**
     * Creates a new user account of the same kind as the given user.
     * @param type a user whose class decides which kind of account is made
     * @param firstName the new user's first name
     * @param lastName the new user's last name
     * @param email the new user's email address
     * @param location where the new user lives
     * @param birthDate the new user's date of birth
     * @param availability true if a volunteer is available
     * @param travelDistance how far a volunteer will travel
     * @param skills a volunteer's skills
     * @param credentials a volunteer's credentials
     * @param supplies a volunteer's supplies
     * @param isVerified true if a volunteer is verified
     * @param isDispached true if a volunteer is already dispatched
     * @param volunteerHours a volunteer's logged hours
     * @param householdSize an aid requester's household size
     * @param assistanceType the kind of help an aid requester needs
     * @param pastRequests an aid requester's past requests
     */
    public static void createUser(User type, String firstName, String lastName, String email, Location location, LocalDate birthDate,

        boolean availability, double travelDistance, ArrayList<String> skills, ArrayList<Credential> credentials,  
        ArrayList<String> supplies, boolean isVerified, boolean isDispached, double volunteerHours,

        int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        UUID userID = UUID.randomUUID();
        User.makeAccount(type, firstName, lastName, userID, email, type.getPhone(), location, birthDate,
            availability, travelDistance, skills, credentials, supplies, isVerified, isDispached, volunteerHours,
            householdSize, assistanceType, pastRequests);
    }

    /**
     * Adds a user to the list of accounts.
     * @param user the user to add
     */
    public void addUser(User user) {
        users.add(user);
    
    }

    /**
     * Removes a user from the list of accounts.
     * @param user the user to remove
     */
    public void deleteUser(User user) {
        users.remove(user);

    }

    /**
     * Replaces the saved account that has the same ID as the given user.
     * @param user the updated user
     */
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                return;
            }
        }

    }

    /**
     * Gets all of the user accounts.
     * @return list of users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Logs in the user with a matching username and password.
     * @param username the username entered
     * @param password the password entered
     * @return true if the login worked, false otherwise
     */
    public boolean login(String username, String password) {
        for (User user : users) {
            if(user.getUserName().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;

    }

    /**
     * Logs out the current user.
     * @return true if someone was logged out, false if no one was logged in
     */
    public boolean logout() {
        if (currentUser != null) {
            this.currentUser = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieved the current user.
     * @return returns the value stored in currentUser
     */
    public User getCurrentUser() {
        return currentUser;
    }

}
