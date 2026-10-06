package com.model;

import java.util.HashMap;

/**
 * @author Nicolas Gauvin
 */
public class AccountManager {

    private HashMap<Integer, User> users;

    public AccountManager(HashMap<Integer, User> users) {
        this.users = users;
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

}