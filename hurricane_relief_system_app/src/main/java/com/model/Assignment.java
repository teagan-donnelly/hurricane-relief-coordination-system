package com.model;

/**
 * @author Nicolas Gauvin
 */
public class Assignment {

    private int assignmentId;
    private Volunteer volunteer;
    private boolean status;
    private HelpRequest request;
    private double assignedTime;
    private double arrivalTime;
    private boolean saftey;
    private boolean terrain;

    public Assignment(int assignmnetID, Volunteer volunteer, Boolean status, Double assignedTime, 
                      Double arrivalTime, Boolean saftey, Boolean terrian) {
        this.assignmentId = assignmnetID;
        this.volunteer = volunteer;
        this.status = status;
        this.assignedTime = assignedTime;
        this.arrivalTime = arrivalTime;
        this.saftey = saftey;
        this.terrain = terrian;
    }

    public void isAccepted(Boolean status, int assignmentID, Double assignmentTime, double arrivalTime) {

    }

    public void isSafe(Boolean saftey) {

    }

    public void traverseDifficulty(Boolean terrain) {

    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public boolean isStatus() {
        return status;
    }

    public HelpRequest getRequest() {
        return request;
    }

    public double getAssignedTime() {
        return assignedTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public boolean isSaftey() {
        return saftey;
    }

    public boolean isTerrain() {
        return terrain;
    }

}