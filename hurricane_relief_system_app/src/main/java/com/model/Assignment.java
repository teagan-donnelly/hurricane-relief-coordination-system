package com.model;

import java.time.LocalDateTime;

/**
 * @author Nicolas Gauvin
 * Represents a volunteer being assigned to a help request.
 */
public class Assignment {

    private int assignmentId;
    private Volunteer volunteer;
    private AssignmentStatus status;
    private HelpRequest request;
    private LocalDateTime assignedTime;
    private LocalDateTime arrivalTime;
    private boolean saftey;
    private boolean terrain;

    /**
     * Constructor for an assignment.
     * @param assignmentId ID of the assignment
     * @param volunteer Volunteer being assigned
     * @param request Help request the volunteer is assigned to
     * @param status Current status of the assignment
     * @param assignedTime Time the assignment was accepted
     * @param arrivalTime Time the volunteer arrived
     * @param saftey true if the area is safe, false otherwise
     * @param terrain true if the terrain is difficult to get through, false otherwise
     */
    public Assignment(int assignmentId, Volunteer volunteer, HelpRequest request, AssignmentStatus status,
                      LocalDateTime assignedTime, LocalDateTime arrivalTime, boolean saftey, boolean terrain) {
        this.assignmentId = assignmentId;
        this.volunteer = volunteer;
        this.request = request;
        this.status = status;
        this.assignedTime = assignedTime;
        this.arrivalTime = arrivalTime;
        this.saftey = saftey;
        this.terrain = terrain;
    }

    /**
     * Constructor for a brand new assignment that hasn't been accepted yet.
     * @param assignmentId ID of the assignment
     * @param volunteer Volunteer being assigned
     * @param request Help request the volunteer is assigned to
     */
    public Assignment(int assignmentId, Volunteer volunteer, HelpRequest request) {
        this(assignmentId, volunteer, request, AssignmentStatus.OPEN, null, null, true, false);
    }

    /**
     * Marks the assignment as accepted by the volunteer.
     * @param status New status of the assignment
     * @param assignedTime Time the volunteer accepted
     */
    public void isAccepted(AssignmentStatus status, LocalDateTime assignedTime) {
        if (status == null) {
            System.out.println("Status cannot be null.");
            return;
        }

        this.status = status;
        this.assignedTime = assignedTime;

        if (volunteer != null) {
            volunteer.setDispatched(true);
        }

        if (request != null && status == AssignmentStatus.RESPONDING) {
            request.setRequestStatus(RequestStatus.EN_ROUTE);
        }
    }

    /**
     * Records when the volunteer arrives at the request location.
     * @param arrivalTime Time the volunteer arrived
     */
    public void hasArrived(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;

        if (request != null) {
            request.setRequestStatus(RequestStatus.ARRIVED);
        }
    }

    /**
     * Marks the assignment as resolved and frees up the volunteer.
     */
    public void resolve() {
        status = AssignmentStatus.RESOLVED;

        if (volunteer != null) {
            volunteer.setDispatched(false);
        }

        if (request != null) {
            request.setRequestStatus(RequestStatus.COMPLETED);
        }
    }

    /**
     * Sets whether the area is safe for the volunteer.
     * @param saftey true if safe, false otherwise
     */
    public void isSafe(boolean saftey) {
        this.saftey = saftey;
    }

    /**
     * Sets whether the terrain is difficult to get through.
     * @param terrain true if difficult, false otherwise
     */
    public void traverseDifficulty(boolean terrain) {
        this.terrain = terrain;
    }

    // Getters

    public int getAssignmentId() {
        return assignmentId;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public HelpRequest getRequest() {
        return request;
    }

    public LocalDateTime getAssignedTime() {
        return assignedTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public boolean isSaftey() {
        return saftey;
    }

    public boolean isTerrain() {
        return terrain;
    }
}