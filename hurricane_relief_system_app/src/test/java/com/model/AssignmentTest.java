package com.model;

import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for accepting, arriving at, and resolving an {@link Assignment}.
 * @author Jorge Naranjo Jr.
 *
 * 
 * +------------------------------------------------------+----------------------------------------------------------------------------------------------------------------------+
 * | Test                                                 | Reasoning                                                                                                            |
 * +------------------------------------------------------+----------------------------------------------------------------------------------------------------------------------+
 * | newAssignmentStartsOpen                              | A new assignment has not been accepted yet, so it should be open with no accepted or arrival time.                   |
 * | newAssignmentAssumesSafeAreaAndEasyTerrain           | Until a volunteer reports a problem, a new assignment should start out as a safe area with easy terrain.             |
 * | reportingUnsafeAreaMarksAssignmentUnsafe             | If a volunteer reports danger like downed lines or flooding, the assignment must remember it so others are warned.   |
 * | acceptingAssignmentRecordsStatusAndTime              | Coordinators need to know when a volunteer took the job to track how long the family has been waiting.               |
 * | acceptingAssignmentDispatchesVolunteer               | A volunteer who accepted a job should be marked dispatched so they are not sent to a second family at the same time. |
 * | respondingAssignmentPutsRequestEnRoute               | The family should be able to see that help is on the way once the volunteer starts responding.                       |
 * | acceptingWithoutRespondingLeavesRequestStatus        | Only a responding volunteer is actually on the way, so other statuses should not tell the family help is coming.     |
 * | acceptingWithNullStatusChangesNothing                | A missing status is invalid input, so it should not dispatch the volunteer or change the assignment.                 |
 * | acceptingWithoutVolunteerOrRequestStillRecordsStatus | An assignment with missing details should still save its status instead of crashing the system.                      |
 * | arrivingRecordsArrivalTime                           | Recording when the volunteer arrived gives coordinators an accurate timeline of the response.                        |
 * | arrivingMarksRequestArrived                          | The family and coordinators need to know the volunteer has reached the location.                                     |
 * | resolvingCompletedRequestResolvesAssignment          | Once the shelter manager completes the request, the job is finished and the assignment should be marked resolved.    |
 * | resolvingAssignmentFreesVolunteer                    | A volunteer who finished a job should be freed so they can be sent to the next family.                               |
 * | resolvingWithoutVolunteerOrRequestSucceeds           | An assignment missing its volunteer or request should still be able to close without crashing.                       |
 * | volunteerCannotResolveAssignment                     | Only the shelter manager can confirm the family arrived safely, so a volunteer should not be able to close the job.  |
 * | failedResolveKeepsVolunteerDispatched                | If the assignment is not resolved, the volunteer is still on that job and should not be given another one.           |
 * +------------------------------------------------------+----------------------------------------------------------------------------------------------------------------------+
 */
public class AssignmentTest {

    private static final LocalDateTime ACCEPTED_AT = LocalDateTime.of(2026, 9, 28, 14, 30);
    private static final LocalDateTime ARRIVED_AT = LocalDateTime.of(2026, 9, 28, 14, 55);

    private Volunteer volunteer;
    private HelpRequest request;

    /**
     * Sets up an available volunteer and a request waiting for them.
     */
    @Before
    public void setUp() {
        volunteer = new Volunteer(true, new ArrayList<String>(), new ArrayList<Credential>());
        request = request(false);
    }

    /**
     * Makes a test request that has been assigned to a volunteer.
     * @param canComplete true if the request should let itself be completed
     * @return Test request
     */
    private HelpRequest request(boolean canComplete) {
        Location carterHome = new Location(32.7765, -79.9311, "14 Tradd St", 0, "Charleston", "SC", "29401");
        if (!canComplete) {
            return new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, null,
                "Need transport to a shelter", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
                RequestStatus.ASSIGNED, carterHome, "street.jpg");
        }
        // markComplete looks the shelter up in the saved data, so it is skipped here
        return new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, null,
            "Need transport to a shelter", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.ASSIGNED, carterHome, "street.jpg") {
            public boolean markComplete(User user) {
                return true;
            }
        };
    }

    /**
     * Tests that a new assignment starts open with no times recorded.
     */
    @Test
    public void newAssignmentStartsOpen() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assertEquals(AssignmentStatus.OPEN, assignment.getStatus());
        assertNull(assignment.getAssignedTime());
        assertNull(assignment.getArrivalTime());
    }

    /**
     * Tests that a new assignment assumes the area is safe and the terrain is easy until told otherwise.
     */
    @Test
    public void newAssignmentAssumesSafeAreaAndEasyTerrain() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assertTrue(assignment.isSaftey());
        assertFalse(assignment.isTerrain());
    }

    /**
     * Tests that reporting an unsafe area is remembered on the assignment.
     */
    @Test
    public void reportingUnsafeAreaMarksAssignmentUnsafe() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assignment.isSafe(false);
        assertFalse(assignment.isSaftey());
    }

    /**
     * Tests that accepting an assignment saves the new status and the time it was accepted.
     */
    @Test
    public void acceptingAssignmentRecordsStatusAndTime() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assignment.isAccepted(AssignmentStatus.RESPONDING, ACCEPTED_AT);
        assertEquals(AssignmentStatus.RESPONDING, assignment.getStatus());
        assertEquals(ACCEPTED_AT, assignment.getAssignedTime());
    }

    /**
     * Tests that accepting an assignment marks the volunteer as dispatched.
     */
    @Test
    public void acceptingAssignmentDispatchesVolunteer() {
        new Assignment(1, volunteer, request).isAccepted(AssignmentStatus.RESPONDING, ACCEPTED_AT);
        assertTrue(volunteer.isDispatched());
    }

    /**
     * Tests that a responding assignment puts the request en route.
     */
    @Test
    public void respondingAssignmentPutsRequestEnRoute() {
        new Assignment(1, volunteer, request).isAccepted(AssignmentStatus.RESPONDING, ACCEPTED_AT);
        assertEquals(RequestStatus.EN_ROUTE, request.getRequestStatus());
    }

    /**
     * Tests that accepting with a status other than responding does not move the request.
     */
    @Test
    public void acceptingWithoutRespondingLeavesRequestStatus() {
        new Assignment(1, volunteer, request).isAccepted(AssignmentStatus.OPEN, ACCEPTED_AT);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that accepting with no status changes nothing.
     */
    @Test
    public void acceptingWithNullStatusChangesNothing() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assignment.isAccepted(null, ACCEPTED_AT);
        assertEquals(AssignmentStatus.OPEN, assignment.getStatus());
        assertNull(assignment.getAssignedTime());
        assertFalse(volunteer.isDispatched());
    }

    /**
     * Tests that an assignment with no volunteer or request can still be accepted.
     */
    @Test
    public void acceptingWithoutVolunteerOrRequestStillRecordsStatus() {
        Assignment assignment = new Assignment(1, null, null);
        assignment.isAccepted(AssignmentStatus.RESPONDING, ACCEPTED_AT);
        assertEquals(AssignmentStatus.RESPONDING, assignment.getStatus());
    }

    /**
     * Tests that arriving saves the time the volunteer arrived.
     */
    @Test
    public void arrivingRecordsArrivalTime() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assignment.hasArrived(ARRIVED_AT);
        assertEquals(ARRIVED_AT, assignment.getArrivalTime());
    }

    /**
     * Tests that arriving marks the request as arrived.
     */
    @Test
    public void arrivingMarksRequestArrived() {
        new Assignment(1, volunteer, request).hasArrived(ARRIVED_AT);
        assertEquals(RequestStatus.ARRIVED, request.getRequestStatus());
    }

    /**
     * Tests that an assignment is resolved when the request can be completed.
     */
    @Test
    public void resolvingCompletedRequestResolvesAssignment() {
        Assignment assignment = new Assignment(1, volunteer, request(true));
        assertTrue(assignment.resolve(null));
        assertEquals(AssignmentStatus.RESOLVED, assignment.getStatus());
    }

    /**
     * Tests that resolving an assignment frees the volunteer for new requests.
     */
    @Test
    public void resolvingAssignmentFreesVolunteer() {
        Assignment assignment = new Assignment(1, volunteer, request(true));
        assignment.isAccepted(AssignmentStatus.RESPONDING, ACCEPTED_AT);
        assignment.resolve(null);
        assertFalse(volunteer.isDispatched());
    }

    /**
     * Tests that an assignment with no volunteer or request can still be arrived at and resolved.
     */
    @Test
    public void resolvingWithoutVolunteerOrRequestSucceeds() {
        Assignment assignment = new Assignment(1, null, null);
        assignment.hasArrived(ARRIVED_AT);
        assertTrue(assignment.resolve(null));
        assertEquals(AssignmentStatus.RESOLVED, assignment.getStatus());
    }

    /**
     * Tests that a volunteer cannot resolve an assignment, because only the shelter manager can complete the request.
     */
    @Test
    public void volunteerCannotResolveAssignment() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assignment.isAccepted(AssignmentStatus.RESPONDING, ACCEPTED_AT);
        assertFalse(assignment.resolve(volunteer));
        assertEquals(AssignmentStatus.RESPONDING, assignment.getStatus());
    }

    /**
     * Tests that a failed resolve keeps the volunteer dispatched.
     */
    @Test
    public void failedResolveKeepsVolunteerDispatched() {
        Assignment assignment = new Assignment(1, volunteer, request);
        assignment.isAccepted(AssignmentStatus.RESPONDING, ACCEPTED_AT);
        assignment.resolve(volunteer);
        assertTrue(volunteer.isDispatched());
    }
}
