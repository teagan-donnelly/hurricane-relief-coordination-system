package com.model;

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the behavior of a {@link HelpRequest}.
 * @author Jorge Naranjo Jr.
 * Additional tests and table explanations prepared with AI assistance.
 *
 * +----------------------------------------------------+------------------------------------------------------------------------------------------------------------------------+
 * | Test                                               | Reasoning                                                                                                              |
 * +----------------------------------------------------+------------------------------------------------------------------------------------------------------------------------+
 * | cancelingPendingRequestSetsStatusToCanceled        | A requester might cancel a pending request because they no longer need assistance or found help elsewhere.             |
 * | cancelingInProgressRequestSetsStatusToCanceled     | Canceling an in-progress request prevents volunteers from wasting time and resources on help that is no longer needed. |
 * | cancelingCompletedRequestThrowsException           | A completed request cannot be canceled because the assistance has already been provided.                               |
 * | failedCancelLeavesCompletedRequestCompleted        | Checking the status ensures that a failed cancellation does not accidentally change the completed request.             |
 * | updatingOpenRequestChangesComment                  | A requester might need to update their comment to provide new information or clarify their situation.                  |
 * | updatingOpenRequestChangesAssistanceType           | During a hurricane, someone's needs can change as conditions worsen or new problems arise.                             |
 * | updatingOpenRequestChangesLocation                 | Updating the location ensures volunteers arrive at the correct address instead of wasting time going somewhere else.   |
 * | updatingOpenRequestChangesPhoto                    | An updated photo helps coordinators better understand the situation and determine what assistance is needed.           |
 * | updatingCanceledRequestThrowsException             | Canceled requests should not be edited because they are no longer active and do not require assistance.                |
 * | updatingCompletedRequestThrowsException            | Completed requests should remain unchanged to preserve an accurate record of the assistance provided.                  |
 * | failedUpdateLeavesCommentUnchanged                 | Checking the comment ensures that a failed update does not accidentally modify the original information.               |
 * | highPriorityRequestIsUrgent                        | High-priority requests should be recognized as urgent when coordinators review requests.                               |
 * | urgentPriorityRequestIsUrgent                      | The highest priority level should also be recognized as urgent.                                                        |
 * | moderatePriorityRequestIsNotUrgent                 | Moderate-priority requests should not be classified as urgent.                                                         |
 * | lowPriorityRequestIsNotUrgent                      | Low-priority requests should not be classified as urgent.                                                              |
 * | nullUrgencyIsNotUrgent                             | A missing urgency value should return false without causing an exception.                                              |
 * | nullUserCannotCompleteRequest                      | Completing a request requires a user with permission, so a missing user must be rejected.                              |
 * | volunteerCannotCompleteRequest                     | The volunteer role cannot complete relief requests, even when the request is assigned.                                 |
 * | aidRequesterCannotCompleteRequest                  | The aid requester role cannot mark a relief request complete.                                                          |
 * | aidCoordinatorCannotCompleteRequest                | An aid coordinator does not have the shelter manager role required to complete a request.                              |
 * | volunteerCoordinatorCannotCompleteRequest          | A volunteer coordinator does not have the shelter manager role required to complete a request.                         |
 * | coordinatorWithoutRoleCannotCompleteRequest        | A coordinator without a role must not receive completion permission.                                                   |
 * | shelterManagerCannotCompleteCanceledRequest        | Having the shelter manager role does not allow a canceled request to be completed.                                     |
 * | shelterManagerCannotCompleteCompletedRequest       | A request that is already complete should reject another completion attempt.                                           |
 * | shelterManagerCannotCompleteRequestWithoutShelter  | A shelter must be selected before its manager can complete the request.                                                |
 * | settingAssignedStatusUpdatesRequest                | The status method should still allow the request to move to an ordinary status such as assigned.                       |
 * | settingCompletedStatusDirectlyKeepsRequestAssigned | Direct status changes must not bypass the shelter manager completion check.                                            |
 * +----------------------------------------------------+------------------------------------------------------------------------------------------------------------------------+
 */
public class HelpRequestTest {

    private Location carterHome;
    private Location newAddress;

    /**
     * Sets up the two addresses used by the tests.
     */
    @Before
    public void setUp() {
        carterHome = new Location(32.7765, -79.9311, "14 Tradd St", 0, "Charleston", "SC", "29401");
        newAddress = new Location(32.7900, -79.9400, "200 Meeting St", 0, "Charleston", "SC", "29401");
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
     * Makes a test coordinator with the given role.
     * @param role Role of the coordinator
     * @return Test coordinator
     */
    private Coordinator coordinator(CoordinatorRole role) {
        return new Coordinator(role, "Felix", "Moreno", "felix@example.com",
            "843-555-0189", "felixmoreno", "demo123", null);
    }

    /**
     * Tests that a pending request becomes canceled.
     */
    @Test
    public void cancelingPendingRequestSetsStatusToCanceled() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        request.cancelRequest();
        assertEquals(RequestStatus.CANCELED, request.getRequestStatus());
    }

    /**
     * Tests that a request already being worked on can still be canceled.
     */
    @Test
    public void cancelingInProgressRequestSetsStatusToCanceled() {
        HelpRequest request = request(RequestStatus.IN_PROGRESS);
        request.cancelRequest();
        assertEquals(RequestStatus.CANCELED, request.getRequestStatus());
    }

    /**
     * Tests that a completed request cannot be canceled.
     */
    @Test(expected = IllegalStateException.class)
    public void cancelingCompletedRequestThrowsException() {
        request(RequestStatus.COMPLETED).cancelRequest();
    }

    /**
     * Tests that a completed request keeps its status after a failed cancel.
     */
    @Test
    public void failedCancelLeavesCompletedRequestCompleted() {
        HelpRequest request = request(RequestStatus.COMPLETED);
        try {
            request.cancelRequest();
            fail("Canceling a completed request should throw an exception.");
        } catch (IllegalStateException e) {
            // expected
        }
        assertEquals(RequestStatus.COMPLETED, request.getRequestStatus());
    }

    /**
     * Tests that updating an open request changes its comment.
     */
    @Test
    public void updatingOpenRequestChangesComment() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        request.updateRequest("Water is rising, need a boat", AssistanceType.RESCUE, newAddress, "flood.jpg");
        assertEquals("Water is rising, need a boat", request.getComment());
    }

    /**
     * Tests that updating an open request changes its assistance type.
     */
    @Test
    public void updatingOpenRequestChangesAssistanceType() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        request.updateRequest("Water is rising, need a boat", AssistanceType.RESCUE, newAddress, "flood.jpg");
        assertEquals(AssistanceType.RESCUE, request.getAssistanceType());
    }

    /**
     * Tests that updating an open request changes its location.
     */
    @Test
    public void updatingOpenRequestChangesLocation() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        request.updateRequest("Water is rising, need a boat", AssistanceType.RESCUE, newAddress, "flood.jpg");
        assertSame(newAddress, request.getLocation());
    }

    /**
     * Tests that updating an open request changes its photo.
     */
    @Test
    public void updatingOpenRequestChangesPhoto() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        request.updateRequest("Water is rising, need a boat", AssistanceType.RESCUE, newAddress, "flood.jpg");
        assertEquals("flood.jpg", request.getPhoto());
    }

    /**
     * Tests that a canceled request cannot be edited.
     */
    @Test(expected = IllegalStateException.class)
    public void updatingCanceledRequestThrowsException() {
        request(RequestStatus.CANCELED).updateRequest("New comment", AssistanceType.RESCUE, newAddress, "flood.jpg");
    }

    /**
     * Tests that a completed request cannot be edited.
     */
    @Test(expected = IllegalStateException.class)
    public void updatingCompletedRequestThrowsException() {
        request(RequestStatus.COMPLETED).updateRequest("New comment", AssistanceType.RESCUE, newAddress, "flood.jpg");
    }

    /**
     * Tests that a failed edit leaves the original comment in place.
     */
    @Test
    public void failedUpdateLeavesCommentUnchanged() {
        HelpRequest request = request(RequestStatus.CANCELED);
        try {
            request.updateRequest("New comment", AssistanceType.RESCUE, newAddress, "flood.jpg");
            fail("Updating a canceled request should throw an exception.");
        } catch (IllegalStateException e) {
            // expected
        }
        assertEquals("Need transport to a shelter", request.getComment());
    }

    /**
     * Tests that high priority is considered urgent.
     */
    @Test
    public void highPriorityRequestIsUrgent() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        assertTrue(request.isUrgent(Urgency.HIGH_PRIORITY));
    }

    /**
     * Tests that urgent priority is considered urgent.
     */
    @Test
    public void urgentPriorityRequestIsUrgent() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        assertTrue(request.isUrgent(Urgency.URGENT_PRIORITY));
    }

    /**
     * Tests that moderate priority is not considered urgent.
     */
    @Test
    public void moderatePriorityRequestIsNotUrgent() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        assertFalse(request.isUrgent(Urgency.MODERATE_PRIORITY));
    }

    /**
     * Tests that low priority is not considered urgent.
     */
    @Test
    public void lowPriorityRequestIsNotUrgent() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        assertFalse(request.isUrgent(Urgency.LOW_PRIORITY));
    }

    /**
     * Tests that a missing urgency is not considered urgent.
     */
    @Test
    public void nullUrgencyIsNotUrgent() {
        HelpRequest request = request(RequestStatus.PENDING_REVIEW);
        assertFalse(request.isUrgent(null));
    }

    /**
     * Tests that a missing user cannot complete a request.
     */
    @Test
    public void nullUserCannotCompleteRequest() {
        HelpRequest request = request(RequestStatus.ASSIGNED);
        assertFalse(request.markComplete(null));
    }

    /**
     * Tests that a volunteer cannot complete a request.
     */
    @Test
    public void volunteerCannotCompleteRequest() {
        Volunteer volunteer = new Volunteer(true, new ArrayList<String>(),
            new ArrayList<Credential>());
        HelpRequest request = request(RequestStatus.ASSIGNED);
        assertFalse(request.markComplete(volunteer));
    }

    /**
     * Tests that an aid requester cannot complete a request.
     */
    @Test
    public void aidRequesterCannotCompleteRequest() {
        AidRequester requester = new AidRequester("Michael", "Carter", UUID.randomUUID(),
            "michael@example.com", "843-555-0147", carterHome,
            LocalDate.of(1987, 4, 12), "michaelcarter", "demo123",
            NotificationType.EMAIL, new ArrayList<String>(), 3,
            AssistanceType.TRANSPORT, new ArrayList<String>());
        HelpRequest request = request(RequestStatus.ASSIGNED);
        assertFalse(request.markComplete(requester));
    }

    /**
     * Tests that an aid coordinator cannot complete a request.
     */
    @Test
    public void aidCoordinatorCannotCompleteRequest() {
        HelpRequest request = request(RequestStatus.ASSIGNED);
        assertFalse(request.markComplete(coordinator(CoordinatorRole.AID)));
    }

    /**
     * Tests that a volunteer coordinator cannot complete a request.
     */
    @Test
    public void volunteerCoordinatorCannotCompleteRequest() {
        HelpRequest request = request(RequestStatus.ASSIGNED);
        assertFalse(request.markComplete(coordinator(CoordinatorRole.VOLUNTEER)));
    }

    /**
     * Tests that a coordinator without a role cannot complete a request.
     */
    @Test
    public void coordinatorWithoutRoleCannotCompleteRequest() {
        HelpRequest request = request(RequestStatus.ASSIGNED);
        assertFalse(request.markComplete(coordinator(null)));
    }

    /**
     * Tests that a shelter manager cannot complete a canceled request.
     */
    @Test
    public void shelterManagerCannotCompleteCanceledRequest() {
        HelpRequest request = request(RequestStatus.CANCELED);
        assertFalse(request.markComplete(coordinator(CoordinatorRole.SHELTER)));
    }

    /**
     * Tests that a shelter manager cannot complete an already completed request.
     */
    @Test
    public void shelterManagerCannotCompleteCompletedRequest() {
        HelpRequest request = request(RequestStatus.COMPLETED);
        assertFalse(request.markComplete(coordinator(CoordinatorRole.SHELTER)));
    }

    /**
     * Tests that a shelter must be selected before a manager can complete a request.
     */
    @Test
    public void shelterManagerCannotCompleteRequestWithoutShelter() {
        HelpRequest request = request(RequestStatus.ASSIGNED);
        assertFalse(request.markComplete(coordinator(CoordinatorRole.SHELTER)));
    }

    /**
     * Tests that the request can be moved to assigned status.
     */
    @Test
    public void settingAssignedStatusUpdatesRequest() {
        HelpRequest request = request(RequestStatus.AWAITING_ASSIGNMENT);
        request.setRequestStatus(RequestStatus.ASSIGNED);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that setting the status directly cannot complete a request.
     */
    @Test
    public void settingCompletedStatusDirectlyKeepsRequestAssigned() {
        HelpRequest request = request(RequestStatus.ASSIGNED);
        request.setRequestStatus(RequestStatus.COMPLETED);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }
}
