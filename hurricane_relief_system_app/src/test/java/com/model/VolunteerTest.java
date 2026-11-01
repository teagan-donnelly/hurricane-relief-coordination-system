package com.model;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for how a {@link Volunteer} accepts, declines, and works on help requests.
 * @author Jorge Naranjo Jr.
 *
 * AI disclosure: the test code and the reasoning table below were drafted with help from Claude (Anthropic).
 *
 * +-------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
 * | Test                                                  | Reasoning                                                                                                       |
 * +-------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
 * | acceptingRequestDispatchesVolunteer                   | A volunteer who accepts a request is now busy and should not be counted as free.                                |
 * | acceptingRequestSetsStatusToAssigned                  | The status lets coordinators and the family see that someone has taken the request.                             |
 * | acceptingRequestRecordsVolunteer                      | Coordinators need to know which volunteer is handling a request in case they need to reach them.                |
 * | unavailableVolunteerCannotAcceptRequest               | A volunteer who marked themselves unavailable should not end up responsible for a family waiting on help.       |
 * | dispatchedVolunteerCannotAcceptSecondRequest          | A volunteer can only be in one place at a time, so taking a second request would leave one family waiting.      |
 * | acceptingCanceledRequestLeavesItCanceled              | A canceled request no longer needs help, so accepting it would waste a volunteer's time.                        |
 * | acceptingCompletedRequestLeavesVolunteerFree          | Help has already been given for a completed request, so the volunteer should stay free for someone else.        |
 * | acceptingNullRequestLeavesVolunteerFree               | Accepting a request that doesn't exist should not mark the volunteer as busy.                                   |
 * | decliningAcceptedRequestReturnsItToAwaitingAssignment | If a volunteer backs out, the request must go back in line so another volunteer can pick it up.                 |
 * | decliningAcceptedRequestFreesVolunteer                | A volunteer who declined a request is free again and should be able to take a different one.                    |
 * | decliningOtherVolunteersRequestChangesNothing         | A volunteer should not be able to take a request away from the volunteer who is actually handling it.           |
 * | decliningNullRequestKeepsVolunteerDispatched          | Declining a request that doesn't exist should not pull the volunteer off the request they are working on.       |
 * | askingForHelpNotifiesAnotherVolunteer                 | Some jobs, like moving a large family, need more than one person, so another free volunteer should be notified. |
 * | askingForHelpSkipsVolunteersWhoCannotHelp             | Unavailable, unverified, or already busy volunteers should not be sent as backup.                               |
 * | cannotAskForHelpOnRequestYouAreNotHandling            | Only the volunteer handling a request should be able to call in more people for it.                             |
 * | completingAssignmentFreesVolunteer                    | After finishing a job, the volunteer should be able to take new requests.                                       |
 * | loggingHoursAddsToTotal                               | Volunteer hours are tracked over time, so new hours should add to the total instead of replacing it.            |
 * | loggingZeroHoursIsIgnored                             | Zero hours is not real work, so it should not change the volunteer's total.                                     |
 * | loggingNegativeHoursIsIgnored                         | Negative hours would wrongly lower a volunteer's total, so they should be rejected.                             |
 * | exportingTripCreatesFile                              | Volunteers may lose cell service in a storm, so they need the trip details saved to a file.                     |
 * | tripSheetListsEveryPassenger                          | The volunteer needs to know everyone they are picking up so no family member is left behind.                    |
 * | tripSheetListsPets                                    | Pets take up room in the vehicle and need a pet friendly shelter, so the volunteer must know about them.        |
 * | tripSheetIncludesMedicalNote                          | Medical needs like an inhaler can be life or death, so the volunteer must see them before pickup.               |
 * | tripSheetLeavesOutMedicalSectionWithoutNote           | An empty medical section could confuse the volunteer, so it should only show up when there is a note.           |
 * | exportingTripWithoutShelterReturnsFalse               | A trip sheet with no destination is useless, so it should fail instead of creating an incomplete file.          |
 * | exportingToMissingFolderReturnsFalse                  | If the file can't be saved, the method should report the failure instead of crashing the app.                   |
 * +-------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
 */
public class VolunteerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Location carterHome;
    private Volunteer maria;
    private ArrayList<Volunteer> savedVolunteers;

    /**
     * Sets up the family's home and an available, verified volunteer.
     */
    @Before
    public void setUp() {
        carterHome = new Location(32.7765, -79.9311, "14 Tradd St", 0, "Charleston", "SC", "29401");
        maria = volunteer("Maria", true, true, false);
    }

    /**
     * Puts back the volunteers that were in the VolunteerManager before the test.
     */
    @After
    public void tearDown() {
        if (savedVolunteers != null) {
            ArrayList<Volunteer> volunteers = VolunteerManager.getInstance().getVolunteers();
            volunteers.clear();
            volunteers.addAll(savedVolunteers);
        }
    }

    /**
     * Makes a test volunteer.
     * @param firstName First name of the volunteer
     * @param available true if the volunteer is available
     * @param verified true if the volunteer is verified
     * @param dispatched true if the volunteer is already dispatched
     * @return Test volunteer
     */
    private Volunteer volunteer(String firstName, boolean available, boolean verified, boolean dispatched) {
        ArrayList<String> supplies = new ArrayList<String>();
        supplies.add("Pickup truck");
        return new Volunteer(firstName, "Lee", UUID.randomUUID(), firstName.toLowerCase() + "@example.com",
            "843-555-0150", new Location(32.8, -79.95, "9 King St", 0, "Charleston", "SC", "29401"),
            LocalDate.of(1995, 3, 14), firstName.toLowerCase(), "pass123", NotificationType.SMS,
            new ArrayList<String>(), available, 25, new ArrayList<String>(), new ArrayList<Credential>(),
            supplies, verified, dispatched, 0);
    }

    /**
     * Makes a test request with the given status.
     * @param status Status the request starts in
     * @return Test request
     */
    private HelpRequest request(RequestStatus status) {
        return new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, null,
            "Need transport to a shelter", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            status, carterHome, "street.jpg");
    }

    /**
     * Makes a test request for a family of 3 with a dog.
     * @return Test request
     */
    private PersonalRequest tripRequest() {
        ArrayList<String> contacts = new ArrayList<String>();
        contacts.add("843-555-0199");
        AidRequester carter = new AidRequester("Carter", "Smith", UUID.randomUUID(), "carter@example.com",
            "843-555-0101", carterHome, LocalDate.of(1980, 6, 2), "csmith", "pass456", NotificationType.SMS,
            contacts, 3, AssistanceType.TRANSPORT, new ArrayList<String>());
        PersonalRequest request = new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, carter,
            "Need transport to a shelter", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.ASSIGNED, carterHome, "street.jpg", 3, "Knock on side door", 1, 1);
        request.addHouseholdMember("Carter Smith");
        request.addHouseholdMember("Ella Smith");
        request.addHouseholdMember("Noah Smith");
        request.addPet("Biscuit");
        return request;
    }

    /**
     * Makes a test shelter.
     * @return Test shelter
     */
    private Shelter shelter() {
        return new Shelter("Burke High School", UUID.randomUUID(),
            new Location(32.7920, -79.9450, "244 President St", 0, "Charleston", "SC", "29403"),
            10, 100, true, new ArrayList<String>(), true, ShelterStatus.OPEN, true);
    }

    /**
     * Replaces the volunteers in the VolunteerManager for one test.
     * @param volunteers Volunteers the manager should have
     */
    private void useVolunteers(Volunteer... volunteers) {
        ArrayList<Volunteer> managed = VolunteerManager.getInstance().getVolunteers();
        savedVolunteers = new ArrayList<Volunteer>(managed);
        managed.clear();
        for (Volunteer volunteer : volunteers) {
            managed.add(volunteer);
        }
    }

    /**
     * Runs an action and collects what it prints.
     * @param action Action to run
     * @return Text the action printed
     */
    private String printed(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream text = new ByteArrayOutputStream();
        System.setOut(new PrintStream(text));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return text.toString();
    }

    /**
     * Exports a trip sheet and reads it back.
     * @param request Request to export
     * @return Text of the trip sheet
     */
    private String exportedTrip(PersonalRequest request) throws Exception {
        File trip = new File(folder.getRoot(), "trip.txt");
        maria.exportTripInfo(request, shelter(), trip.getPath());
        return new String(Files.readAllBytes(trip.toPath()));
    }

    /**
     * Tests that accepting a request marks the volunteer as dispatched.
     */
    @Test
    public void acceptingRequestDispatchesVolunteer() {
        maria.acceptRequest(request(RequestStatus.AWAITING_ASSIGNMENT));
        assertTrue(maria.isDispatched());
    }

    /**
     * Tests that accepting a request sets its status to assigned.
     */
    @Test
    public void acceptingRequestSetsStatusToAssigned() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        maria.acceptRequest(request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that accepting a request saves which volunteer is handling it.
     */
    @Test
    public void acceptingRequestRecordsVolunteer() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        maria.acceptRequest(request);
        assertEquals(maria.getUserId(), request.getAssignedVolunteerID());
    }

    /**
     * Tests that an unavailable volunteer cannot accept a request.
     */
    @Test
    public void unavailableVolunteerCannotAcceptRequest() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        volunteer("Sam", false, true, false).acceptRequest(request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
        assertNull(request.getAssignedVolunteerID());
    }

    /**
     * Tests that a volunteer already on a request cannot accept a second one.
     */
    @Test
    public void dispatchedVolunteerCannotAcceptSecondRequest() {
        maria.acceptRequest(request(RequestStatus.AWAITING_ASSIGNMENT));
        HelpRequest second = request(RequestStatus.AWAITING_ASSIGNMENT);
        maria.acceptRequest(second);
        assertNull(second.getAssignedVolunteerID());
    }

    /**
     * Tests that a canceled request cannot be accepted.
     */
    @Test
    public void acceptingCanceledRequestLeavesItCanceled() {
        HelpRequest request = request(RequestStatus.CANCELED);
        maria.acceptRequest(request);
        assertEquals(RequestStatus.CANCELED, request.getRequestStatus());
        assertFalse(maria.isDispatched());
    }

    /**
     * Tests that a completed request cannot be accepted.
     */
    @Test
    public void acceptingCompletedRequestLeavesVolunteerFree() {
        HelpRequest request = request(RequestStatus.COMPLETED);
        maria.acceptRequest(request);
        assertFalse(maria.isDispatched());
        assertNull(request.getAssignedVolunteerID());
    }

    /**
     * Tests that accepting a missing request does not dispatch the volunteer.
     */
    @Test
    public void acceptingNullRequestLeavesVolunteerFree() {
        maria.acceptRequest(null);
        assertFalse(maria.isDispatched());
    }

    /**
     * Tests that declining an accepted request puts it back to awaiting assignment.
     */
    @Test
    public void decliningAcceptedRequestReturnsItToAwaitingAssignment() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        maria.acceptRequest(request);
        maria.declineRequest(request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
        assertNull(request.getAssignedVolunteerID());
    }

    /**
     * Tests that declining an accepted request frees the volunteer.
     */
    @Test
    public void decliningAcceptedRequestFreesVolunteer() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        maria.acceptRequest(request);
        maria.declineRequest(request);
        assertFalse(maria.isDispatched());
    }

    /**
     * Tests that declining someone else's request does not take it away from them.
     */
    @Test
    public void decliningOtherVolunteersRequestChangesNothing() {
        Volunteer dan = volunteer("Dan", true, true, false);
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        dan.acceptRequest(request);
        maria.declineRequest(request);
        assertEquals(dan.getUserId(), request.getAssignedVolunteerID());
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that declining a missing request does not take the volunteer off their current request.
     */
    @Test
    public void decliningNullRequestKeepsVolunteerDispatched() {
        maria.acceptRequest(request(RequestStatus.AWAITING_ASSIGNMENT));
        maria.declineRequest(null);
        assertTrue(maria.isDispatched());
    }

    /**
     * Tests that asking for help notifies another available, verified volunteer.
     */
    @Test
    public void askingForHelpNotifiesAnotherVolunteer() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        maria.acceptRequest(request);
        useVolunteers(maria, volunteer("Dan", true, true, false));
        assertTrue(printed(() -> maria.requestAdditionalHelp(request)).contains("Dan Lee was notified"));
    }

    /**
     * Tests that volunteers who are unavailable, unverified, or busy are not asked for help.
     */
    @Test
    public void askingForHelpSkipsVolunteersWhoCannotHelp() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        maria.acceptRequest(request);
        useVolunteers(maria, volunteer("Sam", false, true, false), volunteer("Jo", true, false, false),
            volunteer("Ty", true, true, true));
        assertTrue(printed(() -> maria.requestAdditionalHelp(request)).contains("No other volunteers are available"));
    }

    /**
     * Tests that a volunteer cannot ask for help on a request they are not handling.
     */
    @Test
    public void cannotAskForHelpOnRequestYouAreNotHandling() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        useVolunteers(maria, volunteer("Dan", true, true, false));
        assertTrue(printed(() -> maria.requestAdditionalHelp(request)).contains("only ask for help on a request you are handling"));
    }

    /**
     * Tests that completing an assignment frees the volunteer for a new request.
     */
    @Test
    public void completingAssignmentFreesVolunteer() {
        maria.acceptRequest(request(RequestStatus.AWAITING_ASSIGNMENT));
        maria.completeAssignment();
        assertFalse(maria.isDispatched());
    }

    /**
     * Tests that logging hours adds them to the volunteer's total.
     */
    @Test
    public void loggingHoursAddsToTotal() {
        maria.logHours(2.5);
        maria.logHours(4);
        assertEquals(6.5, maria.getVolunteerHours(), 0.001);
    }

    /**
     * Tests that logging zero hours is ignored.
     */
    @Test
    public void loggingZeroHoursIsIgnored() {
        maria.logHours(0);
        assertEquals(0, maria.getVolunteerHours(), 0.001);
    }

    /**
     * Tests that logging negative hours is ignored.
     */
    @Test
    public void loggingNegativeHoursIsIgnored() {
        maria.logHours(3);
        maria.logHours(-2);
        assertEquals(3, maria.getVolunteerHours(), 0.001);
    }

    /**
     * Tests that exporting a trip creates the trip sheet file.
     */
    @Test
    public void exportingTripCreatesFile() {
        File trip = new File(folder.getRoot(), "trip.txt");
        assertTrue(maria.exportTripInfo(tripRequest(), shelter(), trip.getPath()));
        assertTrue(trip.exists());
    }

    /**
     * Tests that the trip sheet lists every family member being picked up.
     */
    @Test
    public void tripSheetListsEveryPassenger() throws Exception {
        String trip = exportedTrip(tripRequest());
        assertTrue(trip.contains("Carter Smith"));
        assertTrue(trip.contains("Ella Smith"));
        assertTrue(trip.contains("Noah Smith"));
    }

    /**
     * Tests that the trip sheet lists the family's pets.
     */
    @Test
    public void tripSheetListsPets() throws Exception {
        assertTrue(exportedTrip(tripRequest()).contains("Biscuit"));
    }

    /**
     * Tests that the trip sheet shows the medical note when there is one.
     */
    @Test
    public void tripSheetIncludesMedicalNote() throws Exception {
        PersonalRequest request = tripRequest();
        request.setMedicalNote("Noah uses an inhaler for asthma");
        assertTrue(exportedTrip(request).contains("Noah uses an inhaler for asthma"));
    }

    /**
     * Tests that the trip sheet leaves out the medical section when there is no note.
     */
    @Test
    public void tripSheetLeavesOutMedicalSectionWithoutNote() throws Exception {
        assertFalse(exportedTrip(tripRequest()).contains("MEDICAL NOTE"));
    }

    /**
     * Tests that exporting a trip without a shelter fails.
     */
    @Test
    public void exportingTripWithoutShelterReturnsFalse() {
        File trip = new File(folder.getRoot(), "trip.txt");
        assertFalse(maria.exportTripInfo(tripRequest(), null, trip.getPath()));
        assertFalse(trip.exists());
    }

    /**
     * Tests that exporting to a folder that does not exist fails instead of crashing.
     */
    @Test
    public void exportingToMissingFolderReturnsFalse() {
        File trip = new File(folder.getRoot(), "missing/trip.txt");
        assertFalse(maria.exportTripInfo(tripRequest(), shelter(), trip.getPath()));
    }
}
