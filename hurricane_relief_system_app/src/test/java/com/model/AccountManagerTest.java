package com.model;

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the behavior of an {@link AccountManager}.
 *
 * +----------------------------------------+----------------------------------------------------------------------------------------+
 * | Test                                   | Explanation                                                                            |
 * +----------------------------------------+----------------------------------------------------------------------------------------+
 * | addingUserStoresAccount                | A newly added account should be available in the manager's list.                       |
 * | addingAnotherUserKeepsExistingAccount  | Adding an account should not replace an unrelated account.                             |
 * | deletingUserRemovesAccount             | Deleting a stored account should remove it from the managed list.                      |
 * | deletingUserKeepsOtherAccounts         | Removing one account should not remove another person's account.                       |
 * | deletingMissingUserLeavesListUnchanged | An absent account should not cause another account to be removed.                      |
 * | updatingMatchingIdReplacesAccount      | An updated object with an equal UUID should replace the stored account.                |
 * | updatingUserKeepsOtherAccounts         | Updating one UUID should not replace an unrelated account.                             |
 * | updatingUnknownUserDoesNotAddAccount   | The update method replaces existing accounts rather than registering new ones.         |
 * | updatedPasswordAllowsNewPasswordLogin  | Replacing account details should make the new password usable.                         |
 * | updatedPasswordRejectsOldPasswordLogin | Login should use the replacement account's password.                                   |
 * | correctCredentialsAllowLogin           | A stored account with matching credentials should authenticate successfully.           |
 * | successfulLoginSetsCurrentUser         | The manager should remember which account successfully logged in.                      |
 * | incorrectPasswordRejectsLogin          | A correct username alone is not enough to authenticate.                                |
 * | unknownUsernameRejectsLogin            | Login should not authenticate an account absent from the managed list.                 |
 * | emptyUserListRejectsLogin              | There is no account to authenticate when the managed list is empty.                    |
 * | loginFindsUserAfterNonmatchingAccount  | A nonmatching first account must not prevent a later matching account from logging in. |
 * | nullUsernameRejectsLogin               | A missing username does not match a stored nonnull username.                           |
 * | nullPasswordRejectsLogin               | A missing password does not match the stored password.                                 |
 * | failedLoginDoesNotStartSession         | A rejected login from a logged-out state should leave no current user.                 |
 * | loggingOutLoggedInUserReturnsTrue      | An existing login session should be successfully logged out.                           |
 * | loggingOutClearsCurrentUser            | The account should no longer be selected after logout.                                 |
 * | loggingOutWithoutSessionReturnsFalse   | There is no active session to log out when nobody has logged in.                       |
 * +----------------------------------------+----------------------------------------------------------------------------------------+
 */
public class AccountManagerTest {

    private AccountManager manager;
    private ArrayList<User> users;
    private AidRequester user;
    private AidRequester otherUser;
    private Location location;

    /**
     * Sets up an empty manager and two different user accounts.
     */
    @Before
    public void setUp() {
        users = new ArrayList<>();
        manager = new AccountManager(users);
        location = new Location(32.7765, -79.9311, "14 Tradd St", 0,
            "Charleston", "SC", "29401");
        user = user(UUID.fromString("00000000-0000-4000-8000-000000000001"),
            "michaelcarter", "demo123");
        otherUser = user(UUID.fromString("00000000-0000-4000-8000-000000000002"),
            "emilycarter", "otherpass123");
    }

    /**
     * Makes a user with the given account details.
     * @param id ID of the user
     * @param username Login name
     * @param password Login password
     * @return Test requester
     */
    private AidRequester user(UUID id, String username, String password) {
        return new AidRequester("Michael", "Carter", id, "carter@example.com",
            "843-555-0147", location, LocalDate.of(1987, 4, 12), username, password,
            NotificationType.EMAIL, new ArrayList<String>(), 3,
            AssistanceType.TRANSPORT, new ArrayList<String>());
    }

    /**
     * Tests that adding a user stores the account.
     */
    @Test
    public void addingUserStoresAccount() {
        manager.addUser(user);
        assertTrue(users.contains(user));
    }

    /**
     * Tests that adding another account keeps the existing account.
     */
    @Test
    public void addingAnotherUserKeepsExistingAccount() {
        manager.addUser(user);
        manager.addUser(otherUser);
        assertTrue(users.contains(user));
    }

    /**
     * Tests that deleting a user removes the account.
     */
    @Test
    public void deletingUserRemovesAccount() {
        manager.addUser(user);
        manager.deleteUser(user);
        assertFalse(users.contains(user));
    }

    /**
     * Tests that deleting one account keeps the other accounts.
     */
    @Test
    public void deletingUserKeepsOtherAccounts() {
        manager.addUser(user);
        manager.addUser(otherUser);
        manager.deleteUser(user);
        assertTrue(users.contains(otherUser));
    }

    /**
     * Tests that deleting an unknown account leaves the list unchanged.
     */
    @Test
    public void deletingMissingUserLeavesListUnchanged() {
        manager.addUser(user);
        manager.deleteUser(otherUser);
        assertEquals(1, users.size());
    }

    /**
     * Tests that an account is updated by matching its ID.
     */
    @Test
    public void updatingMatchingIdReplacesAccount() {
        manager.addUser(user);
        UUID matchingId = UUID.fromString(user.getUserId().toString());
        AidRequester updated = user(matchingId, "michaelcarter", "newpass123");
        manager.updateUser(updated);
        assertSame(updated, users.get(0));
    }

    /**
     * Tests that updating one account keeps the other accounts.
     */
    @Test
    public void updatingUserKeepsOtherAccounts() {
        manager.addUser(user);
        manager.addUser(otherUser);
        AidRequester updated = user(user.getUserId(), "michaelcarter", "newpass123");
        manager.updateUser(updated);
        assertSame(otherUser, users.get(1));
    }

    /**
     * Tests that updating an unknown account does not add it.
     */
    @Test
    public void updatingUnknownUserDoesNotAddAccount() {
        manager.addUser(user);
        manager.updateUser(otherUser);
        assertFalse(users.contains(otherUser));
    }

    /**
     * Tests that the updated password can be used to log in.
     */
    @Test
    public void updatedPasswordAllowsNewPasswordLogin() {
        manager.addUser(user);
        AidRequester updated = user(user.getUserId(), "michaelcarter", "newpass123");
        manager.updateUser(updated);
        assertTrue(manager.login("michaelcarter", "newpass123"));
    }

    /**
     * Tests that the old password stops working after an update.
     */
    @Test
    public void updatedPasswordRejectsOldPasswordLogin() {
        manager.addUser(user);
        AidRequester updated = user(user.getUserId(), "michaelcarter", "newpass123");
        manager.updateUser(updated);
        assertFalse(manager.login("michaelcarter", "demo123"));
    }

    /**
     * Tests that the correct username and password allow login.
     */
    @Test
    public void correctCredentialsAllowLogin() {
        manager.addUser(user);
        assertTrue(manager.login("michaelcarter", "demo123"));
    }

    /**
     * Tests that successful login selects the matching user.
     */
    @Test
    public void successfulLoginSetsCurrentUser() {
        manager.addUser(user);
        manager.login("michaelcarter", "demo123");
        assertSame(user, manager.getCurrentUser());
    }

    /**
     * Tests that an incorrect password rejects login.
     */
    @Test
    public void incorrectPasswordRejectsLogin() {
        manager.addUser(user);
        assertFalse(manager.login("michaelcarter", "wrongpassword"));
    }

    /**
     * Tests that an unknown username rejects login.
     */
    @Test
    public void unknownUsernameRejectsLogin() {
        manager.addUser(user);
        assertFalse(manager.login("missinguser", "demo123"));
    }

    /**
     * Tests that an empty account list rejects login.
     */
    @Test
    public void emptyUserListRejectsLogin() {
        assertFalse(manager.login("michaelcarter", "demo123"));
    }

    /**
     * Tests that login searches beyond the first account.
     */
    @Test
    public void loginFindsUserAfterNonmatchingAccount() {
        manager.addUser(otherUser);
        manager.addUser(user);
        manager.login("michaelcarter", "demo123");
        assertSame(user, manager.getCurrentUser());
    }

    /**
     * Tests that a missing username rejects login.
     */
    @Test
    public void nullUsernameRejectsLogin() {
        manager.addUser(user);
        assertFalse(manager.login(null, "demo123"));
    }

    /**
     * Tests that a missing password rejects login.
     */
    @Test
    public void nullPasswordRejectsLogin() {
        manager.addUser(user);
        assertFalse(manager.login("michaelcarter", null));
    }

    /**
     * Tests that a failed login does not start a new session.
     */
    @Test
    public void failedLoginDoesNotStartSession() {
        manager.addUser(user);
        manager.login("michaelcarter", "wrongpassword");
        assertNull(manager.getCurrentUser());
    }

    /**
     * Tests that logging out an active session returns true.
     */
    @Test
    public void loggingOutLoggedInUserReturnsTrue() {
        manager.addUser(user);
        manager.login("michaelcarter", "demo123");
        assertTrue(manager.logout());
    }

    /**
     * Tests that logging out clears the current user.
     */
    @Test
    public void loggingOutClearsCurrentUser() {
        manager.addUser(user);
        manager.login("michaelcarter", "demo123");
        manager.logout();
        assertNull(manager.getCurrentUser());
    }

    /**
     * Tests that logging out without a session returns false.
     */
    @Test
    public void loggingOutWithoutSessionReturnsFalse() {
        assertFalse(manager.logout());
    }
}
