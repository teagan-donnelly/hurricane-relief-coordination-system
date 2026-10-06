package com.model;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Test;

/**
 * @author Nicolas Gauvin
 * Test class to verify the functionality of saving users and requests via {@link DataWriter}.
 */
public class DataWriterTest {

    /**
     * Tests saving user and request data to persistent storage.
     */
    @Test
    public void testSaveData() {
        System.out.println("=== RUNNING DATAWRITER TEST ===");

        Location mockLocation = new Location(33.9988, -81.0348, "123 Main St", 4, "Columbia", "SC", "29201");

        ArrayList<String> contacts = new ArrayList<String>();
        contacts.add("803-555-0199");

        ArrayList<Volunteer> volunteerList = new ArrayList<Volunteer>();
        VolunteerManager volunteerManager = new VolunteerManager(volunteerList);

        ArrayList<User> testUsers = new ArrayList<User>();

        AidCoordinator testCoordinator = new AidCoordinator(
            "Alice", "Smith", UUID.randomUUID(), "alice@example.com",
            mockLocation, LocalDate.of(1990, 5, 12), "asmith", "pass123",
            NotificationType.EMAIL, contacts, volunteerManager
        );

        AidRequester testRequester = new AidRequester(
            "Bob", "Jones", UUID.randomUUID(), "bob@example.com",
            mockLocation, LocalDate.of(1985, 10, 20), "bjones", "pass456",
            NotificationType.SMS, contacts, 4, AssistanceType.FOOD_SUPPLY, new ArrayList<String>()
        );

        testUsers.add(testCoordinator);
        testUsers.add(testRequester);

        boolean usersSaved = DataWriter.saveUsers(testUsers);
        System.out.println("DataWriter.saveUsers success: " + usersSaved);
        assertTrue("DataWriter.saveUsers should return true on success.", usersSaved);

        ArrayList<HelpRequest> testRequests = new ArrayList<HelpRequest>();

        PersonalRequest personalReq = new PersonalRequest(
            UUID.randomUUID(), RequestType.PERSONAL, testRequester, "Need fresh water",
            AssistanceType.FOOD_SUPPLY, Urgency.HIGH_PRIORITY, RequestStatus.PENDING_REVIEW, mockLocation,
            "photo_water.jpg", 4, "Knock on side door"
        );

        InfrustructureRequest infraReq = new InfrustructureRequest(
            UUID.randomUUID(), RequestType.INFRASTRUCTURE, testRequester, "Downed power line across road",
            AssistanceType.PROPERTY_DAMAGE, Urgency.URGENT_PRIORITY, RequestStatus.IN_PROGRESS, mockLocation,
            "photo_line.jpg", HazardType.ELECTRICAL_HAZARD, LocalDateTime.now()
        );

        testRequests.add(personalReq);
        testRequests.add(infraReq);

        boolean requestsSaved = DataWriter.saveRequests(testRequests);
        System.out.println("DataWriter.saveRequests success: " + requestsSaved);
        assertTrue("DataWriter.saveRequests should return true on success.", requestsSaved);
    }
}