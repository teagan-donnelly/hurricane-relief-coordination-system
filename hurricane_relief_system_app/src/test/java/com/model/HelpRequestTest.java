package com.model;

import static org.junit.Assert.*;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for cancelling and updating a {@link HelpRequest}.
 * @author Jorge Naranjo Jr.
 *
 * +------------------------------------------------+------------------------------------------------------------------------------------------------------------------------+
 * | Test                                           | Reasoning                                                                                                              |
 * +------------------------------------------------+------------------------------------------------------------------------------------------------------------------------+
 * | cancelingPendingRequestSetsStatusToCanceled    | A requester might cancel a pending request because they no longer need assistance or found help elsewhere.             |
 * | cancelingInProgressRequestSetsStatusToCanceled | Canceling an in-progress request prevents volunteers from wasting time and resources on help that is no longer needed. |
 * | cancelingCompletedRequestThrowsException       | A completed request cannot be canceled because the assistance has already been provided.                               |
 * | failedCancelLeavesCompletedRequestCompleted    | Checking the status ensures that a failed cancellation does not accidentally change the completed request.             |
 * | updatingOpenRequestChangesComment              | A requester might need to update their comment to provide new information or clarify their situation.                  |
 * | updatingOpenRequestChangesAssistanceType       | During a hurricane, someone's needs can change as conditions worsen or new problems arise.                             |
 * | updatingOpenRequestChangesLocation             | Updating the location ensures volunteers arrive at the correct address instead of wasting time going somewhere else.   |
 * | updatingOpenRequestChangesPhoto                | An updated photo helps coordinators better understand the situation and determine what assistance is needed.           |
 * | updatingCanceledRequestThrowsException         | Canceled requests should not be edited because they are no longer active and do not require assistance.                |
 * | updatingCompletedRequestThrowsException        | Completed requests should remain unchanged to preserve an accurate record of the assistance provided.                  |
 * | failedUpdateLeavesCommentUnchanged             | Checking the comment ensures that a failed update does not accidentally modify the original information.               |
 * +------------------------------------------------+------------------------------------------------------------------------------------------------------------------------+
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
        } catch (IllegalStateException e) {
            // expected
        }
        assertEquals("Need transport to a shelter", request.getComment());
    }
}
