package com.model;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Gauvin
 * Tests for the behavior of a {@link HurricaneEvent}.
 *
 *
 * +---------------------------------------------------+----------------------------------------------------------------+
 * | Test                                              | Reasoning                                                      |
 * +---------------------------------------------------+----------------------------------------------------------------+
 * | endingEventSetsEndDate                            | Ending the event must successfully record its end date.        |
 * | endingEventBeforeItStartsIsRejected               | Ending the event before its start date must be rejected.       |
 * | addingZipCodeStoresIt                             | Adding a valid zip code must successfully store it.            |
 * | addingSameZipCodeTwiceOnlyStoresItOnce            | Adding the same zip code twice must only store it once.        |
 * | addingZipCodeWithLettersIsRejected                | Adding a zip code containing letters must be rejected.         |
 * | addingZipCodeWithWrongLengthIsRejected            | Adding a zip code with an incorrect length must be rejected.   |
 * | addingZipCodeToEventWithNoZipCodesDoesNotCrash    | Adding a zip code to an event with no zip codes must not crash.|
 * | removingZipCodeDeletesIt                          | Removing a zip code must successfully delete it from the list. |
 * | updatingCategoryNotifiesEveryUser                 | Updating the category must send a notification to every user.  |
 * | updatingCategoryAboveFiveIsRejected               | Updating the category to a value above five must be rejected.  |
 * | updatingCategoryBelowOneIsRejected                | Updating the category to a value below one must be rejected.   |
 * | updatingStatusNotifiesEveryUser                   | Updating the status must send a notification to every user.    |
 * | updatingStatusToNullIsRejected                    | Updating the status to null must be rejected.                  |
 * | notifyingUsersSendsOneMessagePerUser              | Notifying users must send exactly one message per user.        |
 * | notifyingUsersTextsSmsUsers                       | Notifying users must send SMS messages to users who chose SMS. |
 * | notifyingUsersEmailsEmailUsers                    | Notifying users must send emails to users who chose email.     |
 * | notifyingUsersIncludesTheMessage                  | Notifying users must include the specified message text.       |
 * +---------------------------------------------------+----------------------------------------------------------------+
 */
public class HurricaneEventTest {

    private static final int NUMBER_OF_TEST_USERS = 15;

    private HurricaneEvent hurricane;
    private ArrayList<String> zipCodes;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    /**
     * Makes a test hurricane that affects two Charleston zip codes and starts catching printed output.
     */
    @Before
    public void setUp() {
        zipCodes = new ArrayList<String>();
        zipCodes.add("29401");
        zipCodes.add("29403");
        hurricane = new HurricaneEvent(UUID.fromString("71c9e4a2-6b83-4fd0-9a15-3e827c6d504b"),
            "Hurricane Scarlet", 4, HurricaneStatus.WARNING, zipCodes,
            LocalDateTime.of(2026, 9, 22, 6, 0), null, "Expected to make landfall near Charleston, SC");

        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    /**
     * Stops catching printed output.
     */
    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Counts how many notifications were printed.
     * @return Number of lines that say a message was sent
     */
    private int notificationsSent() {
        int count = 0;
        for (String line : output.toString().split("\n")) {
            if (line.contains("message was sent to")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tests that ending the hurricane saves the end date.
     */
    @Test
    public void endingEventSetsEndDate() {
        LocalDateTime end = LocalDateTime.of(2026, 9, 28, 18, 0);
        hurricane.endHurricantEvent(end);
        assertEquals(end, hurricane.getEndDate());
    }

    /**
     * Tests that the hurricane can not end before it started.
     */
    @Test
    public void endingEventBeforeItStartsIsRejected() {
        hurricane.endHurricantEvent(LocalDateTime.of(2026, 9, 1, 12, 0));
        assertNull(hurricane.getEndDate());
    }

    /**
     * Tests that adding a zip code stores it.
     */
    @Test
    public void addingZipCodeStoresIt() {
        hurricane.addAffectedZipCode("29407");
        assertTrue(zipCodes.contains("29407"));
    }

    /**
     * Tests that the same zip code is not stored twice.
     */
    @Test
    public void addingSameZipCodeTwiceOnlyStoresItOnce() {
        hurricane.addAffectedZipCode("29401");
        assertEquals(2, zipCodes.size());
    }

    /**
     * Tests that a zip code with letters in it is not stored.
     */
    @Test
    public void addingZipCodeWithLettersIsRejected() {
        hurricane.addAffectedZipCode("29A07");
        assertFalse(zipCodes.contains("29A07"));
    }

    /**
     * Tests that a zip code that is not five digits is not stored.
     */
    @Test
    public void addingZipCodeWithWrongLengthIsRejected() {
        hurricane.addAffectedZipCode("294");
        assertFalse(zipCodes.contains("294"));
    }

    /**
     * Tests that a hurricane made without a zip code list can still have zip codes added.
     */
    @Test
    public void addingZipCodeToEventWithNoZipCodesDoesNotCrash() {
        HurricaneEvent newStorm = new HurricaneEvent(UUID.randomUUID(), "Hurricane Jasper", 2,
            HurricaneStatus.WATCH, null, LocalDateTime.of(2026, 9, 5, 12, 0), null, "Forming off the coast");
        newStorm.addAffectedZipCode("31401");
    }

    /**
     * Tests that removing a zip code deletes it.
     */
    @Test
    public void removingZipCodeDeletesIt() {
        hurricane.removeAffectedZipCode("29401");
        assertFalse(zipCodes.contains("29401"));
    }

    /**
     * Tests that changing the category sends a notification to every user.
     */
    @Test
    public void updatingCategoryNotifiesEveryUser() {
        hurricane.updateCategory(5);
        assertEquals(NUMBER_OF_TEST_USERS, notificationsSent());
    }

    /**
     * Tests that a category above 5 is not accepted, so nobody is notified.
     */
    @Test
    public void updatingCategoryAboveFiveIsRejected() {
        hurricane.updateCategory(6);
        assertEquals(0, notificationsSent());
    }

    /**
     * Tests that a category below 1 is not accepted, so nobody is notified.
     */
    @Test
    public void updatingCategoryBelowOneIsRejected() {
        hurricane.updateCategory(0);
        assertEquals(0, notificationsSent());
    }

    /**
     * Tests that changing the status sends a notification to every user.
     */
    @Test
    public void updatingStatusNotifiesEveryUser() {
        hurricane.updateStatus(HurricaneStatus.ACTIVE);
        assertEquals(NUMBER_OF_TEST_USERS, notificationsSent());
    }

    /**
     * Tests that a missing status is not accepted, so nobody is notified.
     */
    @Test
    public void updatingStatusToNullIsRejected() {
        hurricane.updateStatus(null);
        assertEquals(0, notificationsSent());
    }

    /**
     * Tests that every user in the system gets exactly one message.
     */
    @Test
    public void notifyingUsersSendsOneMessagePerUser() {
        hurricane.notifyUsers("Evacuate zone A now");
        assertEquals(NUMBER_OF_TEST_USERS, notificationsSent());
    }

    /**
     * Tests that a user who picked SMS gets a text message.
     */
    @Test
    public void notifyingUsersTextsSmsUsers() {
        hurricane.notifyUsers("Evacuate zone A now");
        assertTrue(output.toString().contains("A SMS message was sent to: freed87"));
    }

    /**
     * Tests that a user who picked email gets an email.
     */
    @Test
    public void notifyingUsersEmailsEmailUsers() {
        hurricane.notifyUsers("Evacuate zone A now");
        assertTrue(output.toString().contains("An EMAIL message was sent to: etorres93"));
    }

    /**
     * Tests that the message itself is actually sent to users.
     */
    @Test
    public void notifyingUsersIncludesTheMessage() {
        hurricane.notifyUsers("Evacuate zone A now");
        assertTrue(output.toString().contains("Evacuate zone A now"));
    }
}
