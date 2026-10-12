package com.model;

import java.util.HashMap;

public class AccountManager {
   private HashMap<Integer, User> users;

   public AccountManager(HashMap<Integer, User> users) {
      this.users = users;
   }

   public void addUser(User user) {
    users.put(user.getId(), user);
   }

   public void deleteUser(User user) {
    users.remove(user.getId());
   }

   public void updateUser(User user) {
    users.put(user.getId(), user);
   }

   public HashMap<Integer, User> getUsers() {
      return this.users;
   }
}
