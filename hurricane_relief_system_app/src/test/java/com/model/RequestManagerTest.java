package com.model;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Gauvin
 * Tests for the behavior of a {@link RequestManager}.
 *
 *
 * +--------------------------------------------------------+----------------------------------------------------------------+
 * | Test                                                   | Reasoning                                                      |
 * +--------------------------------------------------------+----------------------------------------------------------------+
 * | getInstanceReturnsSameManager                          | Checks that the instance is actually a singleton.              |
 * | addingValidRequestStoresIt                             | An added request must be properly stored.                      |
 * | addingNullRequestDoesNotChangeList                     | A null request must not add anything to the list.              |
 * | addingRequestWithoutLocationIsRejected                 | A request without a location must not be added.                |
 * | addingDuplicateRequestDoesNotAddSecondCopy             | Duplicate requests must not be counted twoce.                  |
 * | addingDuplicateRequestCombinesComments                 | The comments should be merged instead of being discarded.      |
 * | addingDuplicateRequestKeepsHigherUrgency               | Duplicate requests must keep the highest urgency of the two.   |
 * | addingDuplicateWithLowerUrgencyKeepsExistingUrgency    | Same reasoning as above. Must keep highest urgency.            |
 * | sameRequestAtDifferentAddressIsAdded                   | A request at a different address must be treated as new.       |
 * | sameRequestFromDifferentUserIsAdded                    | A request from a different user must be treated as new.        |
 * | sameRequestWithDifferentAssistanceTypeIsAdded          | A request of different assistance type must be treated as new. |
 * | requestMatchingCompletedRequestIsAdded                 | A request is not a duplicate if the original is completed.     |
 * | requestMatchingCanceledRequestIsAdded                  | A request is not a duplicate if the original was canceled.     |
 * | addingRequestWhenStoredRequestHasNoUserDoesNotCrash    | Requests missing a user must not break new additions.          |
 * | addingRequestWhenStoredRequestHasNoStreetDoesNotCrash  | Requests missing a street address must not break new additions.|
 * | removingRequestDeletesIt                               | Removing a request must properly delete it from the list.      |
 * | removingNullRequestDoesNotChangeList                   | Removing a null request must have no effect on the list.       |
 * | removingMissingRequestDoesNotChangeList                | Removing a request not in the list must not change anything.   |
 * | completeRequestIsValid                                 | A request containing all required fields is valid.             |
 * | nullRequestIsNotValid                                  | A null request is not valid.                                   |
 * | requestWithoutUserIsNotValid                           | A request missing a user is not valid.                         |
 * | requestWithoutAssistanceTypeIsNotValid                 | A request missing an assistance type is not valid.             |
 * | requestWithoutLocationIsNotValid                       | A request missing a location is not valid.                     |
 * | mergingIntoRequestWithNoCommentDoesNotAddNullText      | Merging a request with no comment must not add "null" text.    |
 * | mergingIntoCanceledRequestIsNotAllowed                 | Merging into a canceled request should not work.               |
 * | flaggingRequestSetsPendingReview                       | Flagging a request must update its status to pending review.   |
 * | flaggingNullRequestDoesNotCrash                        | Flagging a null request must handle without crashing.          |
 * | flaggingCompletedRequestKeepsItCompleted               | A completed request cannot be flagged back into review.        |
 * | gettingRequestByIdFindsIt                              | A stored request must be retrievable by its unique ID.         |
 * | gettingRequestWithUnknownIdReturnsNull                 | Looking up an ID that does not exist must return null.         |
 * | sortingPutsMostUrgentRequestFirst                      | Sorting must place the most urgent request at the front.       |
 * | sortingPutsLeastUrgentRequestLast                      | Sorting must place the least urgent request at the back        |
 * | sortingPutsRequestWithNoUrgencyLast                    | A request with no urgency must be sorted last.                 |
 * | sortingKeepsOrderOfEqualUrgencyRequests                | An older request of equal urgency should have precedent.       |
 * | sortingEmptyListDoesNotCrash                           | Sorting an empty list must not cause errors or crash.          |
 * | savingRequestsReturnsTrue                              | Saving the requests to storage must report success.            |
 * | savedRequestsCanBeLoadedAgain                          | Requests that are saved must be successfully reloaded.         |
 * +--------------------------------------------------------+----------------------------------------------------------------+
 */
public class RequestManagerTest {

    private RequestManager manager;
    private byte[] originalRequestFile;
    private AidRequester jane;
    private AidRequester carlos;
    private Location janeHome;
    private Location carlosHome;

    /**
     * Saves a copy of the request test file and fills the manager with known requests.
     */
    @Before
    public void setUp() throws Exception {
        originalRequestFile = Files.readAllBytes(requestFile().toPath());

        janeHome = new Location(32.7765, -79.9311, "123 Coastal Way", 0, "Charleston", "SC", "29401");
        carlosHome = new Location(32.0809, -81.0912, "789 Ocean Blvd", 0, "Savannah", "GA", "31401");
        jane = requester(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"), "Jane", "Doe", janeHome);
        carlos = requester(UUID.fromString("f9e8d7c6-b5a4-3210-9876-543210fedcba"), "Carlos", "Mendoza", carlosHome);

        manager = RequestManager.getInstance();
        manager.getRequests().clear();
    }

    /**
     * Puts the request test file back the way it was.
     */
    @After
    public void tearDown() throws Exception {
        Files.write(requestFile().toPath(), originalRequestFile);
        manager.getRequests().clear();
    }

    /**
     * Finds the test copy of the help request file.
     * @return The help request file
     */
    private File requestFile() throws Exception {
        return new File(RequestManagerTest.class.getResource("resources/data/aid-request.json").toURI());
    }

    /**
     * Makes a test aid requester.
     * @param id ID of the requester
     * @param firstName First name
     * @param lastName Last name
     * @param home Where the requester lives
     * @return Test requester
     */
    private AidRequester requester(UUID id, String firstName, String lastName, Location home) {
        return new AidRequester(firstName, lastName, id, firstName.toLowerCase() + "@example.com",
            "843-555-0101", home, LocalDate.of(1988, 4, 12), firstName.toLowerCase() + "123", "pass123",
            NotificationType.EMAIL, new ArrayList<String>(), 4, AssistanceType.SHELTER, new ArrayList<String>());
    }

    /**
     * Makes a test help request that is waiting for a volunteer.
     * @param user User who made the request
     * @param type Type of assistance needed
     * @param urgency How urgent the request is
     * @param location Where help is needed
     * @param comment Comment on the request
     * @param photo Photo of the damage
     * @return Test request
     */
    private HelpRequest request(User user, AssistanceType type, Urgency urgency, Location location,
                                String comment, String photo) {
        return new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, user, comment, type, urgency,
            RequestStatus.AWAITING_ASSIGNMENT, location, photo);
    }

    /**
     * Makes a test shelter request from Jane with the given urgency.
     * @param urgency How urgent the request is
     * @return Test request
     */
    private HelpRequest janeRequest(Urgency urgency) {
        return request(jane, AssistanceType.SHELTER, urgency, janeHome, "Roof is leaking", "roof.jpg");
    }

    /**
     * Tests that getInstance always gives back the same manager.
     */
    @Test
    public void getInstanceReturnsSameManager() {
        assertSame(manager, RequestManager.getInstance());
    }

    /**
     * Tests that a complete request is added to the list.
     */
    @Test
    public void addingValidRequestStoresIt() {
        HelpRequest request = janeRequest(Urgency.HIGH_PRIORITY);
        manager.addRequest(request);
        assertTrue(manager.getRequests().contains(request));
    }

    /**
     * Tests that adding a missing request does not change the list.
     */
    @Test
    public void addingNullRequestDoesNotChangeList() {
        manager.addRequest(null);
        assertEquals(0, manager.getRequests().size());
    }

    /**
     * Tests that a request without a location is not added.
     */
    @Test
    public void addingRequestWithoutLocationIsRejected() {
        manager.addRequest(request(jane, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, null, "Need help", ""));
        assertEquals(0, manager.getRequests().size());
    }

    /**
     * Tests that the same request sent twice is only stored once.
     */
    @Test
    public void addingDuplicateRequestDoesNotAddSecondCopy() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        manager.addRequest(request(jane, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, janeHome,
            "Water coming in", ""));
        assertEquals(1, manager.getRequests().size());
    }

    /**
     * Tests that the comments of duplicate requests are combined.
     */
    @Test
    public void addingDuplicateRequestCombinesComments() {
        HelpRequest original = janeRequest(Urgency.HIGH_PRIORITY);
        manager.addRequest(original);
        manager.addRequest(request(jane, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, janeHome,
            "Water coming in", ""));
        assertEquals("Roof is leaking | Water coming in", original.getComment());
    }

    /**
     * Tests that merging a more urgent duplicate raises the urgency.
     */
    @Test
    public void addingDuplicateRequestKeepsHigherUrgency() {
        HelpRequest original = janeRequest(Urgency.MODERATE_PRIORITY);
        manager.addRequest(original);
        manager.addRequest(janeRequest(Urgency.URGENT_PRIORITY));
        assertEquals(Urgency.URGENT_PRIORITY, original.getUrgency());
    }

    /**
     * Tests that merging a less urgent duplicate does not lower the urgency.
     */
    @Test
    public void addingDuplicateWithLowerUrgencyKeepsExistingUrgency() {
        HelpRequest original = janeRequest(Urgency.URGENT_PRIORITY);
        manager.addRequest(original);
        manager.addRequest(janeRequest(Urgency.LOW_PRIORITY));
        assertEquals(Urgency.URGENT_PRIORITY, original.getUrgency());
    }

    /**
     * Tests that the same kind of request at another address is a new request.
     */
    @Test
    public void sameRequestAtDifferentAddressIsAdded() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        Location otherHouse = new Location(32.78, -79.93, "45 King St", 0, "Charleston", "SC", "29401");
        manager.addRequest(request(jane, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, otherHouse, "Roof is leaking", ""));
        assertEquals(2, manager.getRequests().size());
    }

    /**
     * Tests that the same kind of request from another person is a new request.
     */
    @Test
    public void sameRequestFromDifferentUserIsAdded() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        manager.addRequest(request(carlos, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, janeHome, "Roof is leaking", ""));
        assertEquals(2, manager.getRequests().size());
    }

    /**
     * Tests that a different kind of help at the same address is a new request.
     */
    @Test
    public void sameRequestWithDifferentAssistanceTypeIsAdded() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        manager.addRequest(request(jane, AssistanceType.MEDICAL, Urgency.HIGH_PRIORITY, janeHome, "Cut my hand", ""));
        assertEquals(2, manager.getRequests().size());
    }

    /**
     * Tests that a request matching a completed request is added as a new request.
     */
    @Test
    public void requestMatchingCompletedRequestIsAdded() {
        HelpRequest finished = new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, jane, "Roof is leaking",
            AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, RequestStatus.COMPLETED, janeHome, "");
        manager.getRequests().add(finished);
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        assertEquals(2, manager.getRequests().size());
    }

    /**
     * Tests that a request matching a canceled request is added as a new request.
     */
    @Test
    public void requestMatchingCanceledRequestIsAdded() {
        HelpRequest canceled = new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, jane, "Roof is leaking",
            AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, RequestStatus.CANCELED, janeHome, "");
        manager.getRequests().add(canceled);
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        assertEquals(2, manager.getRequests().size());
    }

    /**
     * Tests that a stored request with no user does not stop new requests from being added.
     */
    @Test
    public void addingRequestWhenStoredRequestHasNoUserDoesNotCrash() {
        HelpRequest noUser = new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, null, "Tree fell",
            AssistanceType.PROPERTY_DAMAGE, Urgency.HIGH_PRIORITY, RequestStatus.PENDING_REVIEW, carlosHome, "");
        manager.getRequests().add(noUser);
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        assertEquals(2, manager.getRequests().size());
    }

    /**
     * Tests that a stored request with no street address does not stop new requests from being added.
     */
    @Test
    public void addingRequestWhenStoredRequestHasNoStreetDoesNotCrash() {
        Location noStreet = new Location(32.78, -79.93, null, 0, "Charleston", "SC", "29401");
        manager.addRequest(request(jane, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, noStreet, "Roof is leaking", ""));
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        assertEquals(2, manager.getRequests().size());
    }

    /**
     * Tests that removing a request deletes it from the list.
     */
    @Test
    public void removingRequestDeletesIt() {
        HelpRequest request = janeRequest(Urgency.HIGH_PRIORITY);
        manager.addRequest(request);
        manager.removeRequest(request);
        assertFalse(manager.getRequests().contains(request));
    }

    /**
     * Tests that removing a missing request does not change the list.
     */
    @Test
    public void removingNullRequestDoesNotChangeList() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        manager.removeRequest(null);
        assertEquals(1, manager.getRequests().size());
    }

    /**
     * Tests that removing a request that was never added does not remove anything.
     */
    @Test
    public void removingMissingRequestDoesNotChangeList() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        manager.removeRequest(request(carlos, AssistanceType.MEDICAL, Urgency.LOW_PRIORITY, carlosHome, "Need insulin", ""));
        assertEquals(1, manager.getRequests().size());
    }

    /**
     * Tests that a request with a user, assistance type, and location is valid.
     */
    @Test
    public void completeRequestIsValid() {
        assertTrue(manager.validateRequest(janeRequest(Urgency.HIGH_PRIORITY)));
    }

    /**
     * Tests that a missing request is not valid.
     */
    @Test
    public void nullRequestIsNotValid() {
        assertFalse(manager.validateRequest(null));
    }

    /**
     * Tests that a request with no user is not valid.
     */
    @Test
    public void requestWithoutUserIsNotValid() {
        assertFalse(manager.validateRequest(request(null, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, janeHome, "Help", "")));
    }

    /**
     * Tests that a request with no assistance type is not valid.
     */
    @Test
    public void requestWithoutAssistanceTypeIsNotValid() {
        assertFalse(manager.validateRequest(request(jane, null, Urgency.HIGH_PRIORITY, janeHome, "Help", "")));
    }

    /**
     * Tests that a request with no location is not valid.
     */
    @Test
    public void requestWithoutLocationIsNotValid() {
        assertFalse(manager.validateRequest(request(jane, AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, null, "Help", "")));
    }

    /**
     * Tests that merging into a request without a comment does not put the word null in the comment.
     */
    @Test
    public void mergingIntoRequestWithNoCommentDoesNotAddNullText() {
        HelpRequest existing = new HelpRequest(UUID.randomUUID(), jane, AssistanceType.SHELTER,
            Urgency.HIGH_PRIORITY, RequestStatus.AWAITING_ASSIGNMENT, janeHome);
        manager.mergeRequest(existing, janeRequest(Urgency.HIGH_PRIORITY));
        assertFalse(existing.getComment().contains("null"));
    }

    /**
     * Tests that a request that was canceled cannot have another request merged into it.
     */
    @Test(expected = IllegalStateException.class)
    public void mergingIntoCanceledRequestIsNotAllowed() {
        HelpRequest existing = janeRequest(Urgency.HIGH_PRIORITY);
        existing.cancelRequest();
        manager.mergeRequest(existing, janeRequest(Urgency.HIGH_PRIORITY));
    }

    /**
     * Tests that flagging a request sends it back for review.
     */
    @Test
    public void flaggingRequestSetsPendingReview() {
        HelpRequest request = janeRequest(Urgency.HIGH_PRIORITY);
        manager.flagRequest(request);
        assertEquals(RequestStatus.PENDING_REVIEW, request.getRequestStatus());
    }

    /**
     * Tests that flagging a missing request does not crash.
     */
    @Test
    public void flaggingNullRequestDoesNotCrash() {
        manager.flagRequest(null);
        assertEquals(0, manager.getRequests().size());
    }

    /**
     * Tests that a completed request can not be flagged back into review.
     */
    @Test
    public void flaggingCompletedRequestKeepsItCompleted() {
        HelpRequest finished = new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, jane, "Roof is leaking",
            AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, RequestStatus.COMPLETED, janeHome, "");
        manager.flagRequest(finished);
        assertEquals(RequestStatus.COMPLETED, finished.getRequestStatus());
    }

    /**
     * Tests that a request can be found by its ID.
     */
    @Test
    public void gettingRequestByIdFindsIt() {
        HelpRequest first = janeRequest(Urgency.HIGH_PRIORITY);
        HelpRequest second = request(carlos, AssistanceType.MEDICAL, Urgency.URGENT_PRIORITY, carlosHome, "Need insulin", "");
        manager.addRequest(first);
        manager.addRequest(second);
        assertSame(second, manager.getRequest(second.getRequestID()));
    }

    /**
     * Tests that looking up an ID that is not stored returns null.
     */
    @Test
    public void gettingRequestWithUnknownIdReturnsNull() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        assertNull(manager.getRequest(UUID.fromString("00000000-0000-4000-8000-000000000099")));
    }

    /**
     * Tests that sorting puts the most urgent request at the front.
     */
    @Test
    public void sortingPutsMostUrgentRequestFirst() {
        HelpRequest urgent = request(carlos, AssistanceType.MEDICAL, Urgency.URGENT_PRIORITY, carlosHome, "Need insulin", "");
        manager.addRequest(janeRequest(Urgency.LOW_PRIORITY));
        manager.addRequest(urgent);
        manager.sortRequests();
        assertSame(urgent, manager.getRequests().get(0));
    }

    /**
     * Tests that sorting puts the least urgent request at the back.
     */
    @Test
    public void sortingPutsLeastUrgentRequestLast() {
        HelpRequest low = janeRequest(Urgency.LOW_PRIORITY);
        manager.addRequest(low);
        manager.addRequest(request(carlos, AssistanceType.MEDICAL, Urgency.URGENT_PRIORITY, carlosHome, "Need insulin", ""));
        manager.addRequest(request(carlos, AssistanceType.FOOD_SUPPLY, Urgency.MODERATE_PRIORITY, carlosHome, "Need food", ""));
        manager.sortRequests();
        assertSame(low, manager.getRequests().get(2));
    }

    /**
     * Tests that a request with no urgency is sorted after the others.
     */
    @Test
    public void sortingPutsRequestWithNoUrgencyLast() {
        HelpRequest noUrgency = janeRequest(null);
        manager.addRequest(noUrgency);
        manager.addRequest(request(carlos, AssistanceType.MEDICAL, Urgency.LOW_PRIORITY, carlosHome, "Need insulin", ""));
        manager.sortRequests();
        assertSame(noUrgency, manager.getRequests().get(1));
    }

    /**
     * Tests that requests with the same urgency stay in the order they were added.
     */
    @Test
    public void sortingKeepsOrderOfEqualUrgencyRequests() {
        HelpRequest first = janeRequest(Urgency.HIGH_PRIORITY);
        HelpRequest second = request(carlos, AssistanceType.MEDICAL, Urgency.HIGH_PRIORITY, carlosHome, "Need insulin", "");
        manager.addRequest(first);
        manager.addRequest(second);
        manager.sortRequests();
        assertSame(first, manager.getRequests().get(0));
    }

    /**
     * Tests that sorting an empty list does not crash.
     */
    @Test
    public void sortingEmptyListDoesNotCrash() {
        manager.sortRequests();
        assertEquals(0, manager.getRequests().size());
    }

    /**
     * Tests that saving the requests reports success.
     */
    @Test
    public void savingRequestsReturnsTrue() {
        manager.addRequest(janeRequest(Urgency.HIGH_PRIORITY));
        assertTrue(manager.saveRequests());
    }

    /**
     * Tests that saved requests can be read back from the file.
     */
    @Test
    public void savedRequestsCanBeLoadedAgain() {
        HelpRequest request = new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, jane, "Roof is leaking",
            AssistanceType.SHELTER, Urgency.HIGH_PRIORITY, RequestStatus.AWAITING_ASSIGNMENT, janeHome, "", 4, "", 0, 2);
        manager.addRequest(request);
        manager.saveRequests();
        ArrayList<HelpRequest> loaded = DataLoader.getRequests();
        assertEquals(request.getRequestID(), loaded.get(0).getRequestID());
    }
}