package com.model;

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the behavior of a {@link Coordinator}.
 *
 * +-----------------------------------------------------+-----------------------------------------------------------------------------------------+
 * | Test                                                | Explanation                                                                             |
 * +-----------------------------------------------------+-----------------------------------------------------------------------------------------+
 * | approvedCertificationReturnsTrue                    | The method should return the manual approval decision for an existing volunteer.        |
 * | rejectedCertificationReturnsFalse                   | An existing volunteer's certification may be rejected by the coordinator.               |
 * | nullVolunteerCannotHaveCertificationApproved        | A missing volunteer must not receive certification approval.                            |
 * | passedBackgroundCheckReturnsTrue                    | The method should return a successful manual background-check decision.                 |
 * | failedBackgroundCheckReturnsFalse                   | A failed background-check decision should remain false.                                 |
 * | nullVolunteerCannotPassBackgroundCheck              | There must be a volunteer for the background-check decision to apply to.                |
 * | settingUrgencyUpdatesRequest                        | A valid request and urgency should update the urgency stored on that request.           |
 * | settingUrgencyWithoutRequestThrowsException         | A missing request is rejected rather than dereferenced.                                 |
 * | settingNullUrgencyThrowsException                   | The coordinator method rejects a missing urgency value.                                 |
 * | assigningQualifiedVolunteerToApprovedRequestWorks   | APPROVED is one of the two statuses accepted by the coordinator.                        |
 * | assigningQualifiedVolunteerToWaitingRequestWorks    | AWAITING_ASSIGNMENT is also an accepted assignment status.                              |
 * | medicalRequestAcceptsValidEmt                       | Medical requests accept an EMT credential with a valid status.                          |
 * | childInjuryRequestAcceptsValidNurse                 | Child injury requests use the medical credential rules.                                 |
 * | rescueRequestAcceptsSearchAndRescueCredential       | Rescue requests accept a valid search and rescue credential.                            |
 * | floodRequestAcceptsFirefighterCredential            | Flood requests accept a valid firefighter credential.                                   |
 * | foodRequestAcceptsFoodDistributionSkill             | Skill matching should recognize the trimmed skill text regardless of capitalization.    |
 * | shelterRequestAcceptsShelterAssistanceSkill         | Shelter requests require the shelter assistance skill.                                  |
 * | propertyDamageRequestAcceptsConstructionSkill       | Construction is one accepted skill for property damage assistance.                      |
 * | propertyDamageRequestAcceptsDebrisRemovalSkill      | Debris removal is the other accepted property damage skill.                             |
 * | medicalSuppliesRequestAcceptsDeliverySkill          | Medical supplies requests require the matching delivery skill.                          |
 * | petInjuryRequestAcceptsAnimalCareSkill              | Pet injury requests require the animal care skill.                                      |
 * | assigningNullVolunteerKeepsRequestWaiting           | Assignment requires a volunteer object.                                                 |
 * | assigningWithoutRequestKeepsVolunteerUndispatched   | A volunteer must not be dispatched when there is no request.                            |
 * | assigningWithoutManagerKeepsRequestWaiting          | A coordinator without a manager cannot delegate an assignment.                          |
 * | assigningPendingRequestIsRejected                   | The coordinator only accepts approved or awaiting-assignment requests.                  |
 * | assigningUnavailableVolunteerIsRejected             | Availability is required for assignment.                                                |
 * | assigningUnverifiedVolunteerIsRejected              | The coordinator requires a verified volunteer.                                          |
 * | assigningDispatchedVolunteerIsRejected              | A dispatched volunteer is already handling an assignment.                               |
 * | assigningVolunteerWithoutMatchingSkillIsRejected    | A skill list must contain the skill required by the request.                            |
 * | assigningExpiredMedicalCredentialIsRejected         | Matching the credential type is insufficient when its status is not valid.              |
 * | assigningVolunteerWithNullSkillsIsRejected          | A missing skills list should be handled without assignment or a null-pointer exception. |
 * | assigningVolunteerWithNullCredentialsIsRejected     | A missing credentials list should be handled without a null-pointer exception.          |
 * | automaticAssignmentRejectsNullRequest               | Automatic matching needs a request to inspect.                                          |
 * | automaticAssignmentRejectsMissingLocation           | A request must have a location before map matching can run.                             |
 * | automaticAssignmentRejectsMissingAssistanceType     | Qualification filtering requires the request's assistance type.                         |
 * | automaticAssignmentRejectsPendingRequest            | Automatic matching uses the same accepted request statuses as manual assignment.        |
 * | automaticAssignmentRejectsMissingManager            | The coordinator cannot obtain volunteers without a manager.                             |
 * | automaticAssignmentReturnsNullForEmptyVolunteerList | An empty volunteer list provides no eligible candidate.                                 |
 * | automaticAssignmentSkipsNullVolunteer               | A null list entry cannot be inspected as a volunteer.                                   |
 * | automaticAssignmentSkipsVolunteerWithoutLocation    | A volunteer must have a location for map matching.                                      |
 * | automaticAssignmentSkipsUnavailableVolunteer        | Unavailable volunteers are excluded before a map lookup.                                |
 * | automaticAssignmentSkipsUnverifiedVolunteer         | Unverified volunteers are excluded before a map lookup.                                 |
 * | automaticAssignmentSkipsDispatchedVolunteer         | Volunteers handling other assignments are excluded before a map lookup.                 |
 * | automaticAssignmentSkipsUnqualifiedVolunteer        | Only volunteers qualified for the requested assistance are eligible.                    |
 * | loadingVolunteerFixtureReturnsAllFiveVolunteers      | The fixture must load completely instead of returning an empty or partial list.           |
 * +-----------------------------------------------------+-----------------------------------------------------------------------------------------+
 */
public class CoordinatorTest {

    private Coordinator coordinator;
    private VolunteerManager manager;
    private ArrayList<Volunteer> savedVolunteers;
    private Volunteer volunteer;
    private HelpRequest request;
    private Location location;
    private ArrayList<String> skills;
    private ArrayList<Credential> credentials;

    /**
     * Sets up a coordinator, volunteer, and request for each test.
     */
    @Before
    public void setUp() {
        manager = VolunteerManager.getInstance();
        savedVolunteers = new ArrayList<>(manager.getVolunteers());
        manager.getVolunteers().clear();

        location = new Location(32.7765, -79.9311, "14 Tradd St", 0,
            "Charleston", "SC", "29401");
        skills = new ArrayList<>();
        skills.add("Transportation");
        credentials = new ArrayList<>();
        volunteer = volunteer(true, true, false, location);
        coordinator = coordinator(manager);
        request = request(AssistanceType.TRANSPORT, RequestStatus.AWAITING_ASSIGNMENT);
    }

    /**
     * Restores the volunteer list so other tests keep their original data.
     */
    @After
    public void tearDown() {
        if (manager != null && savedVolunteers != null) {
            manager.getVolunteers().clear();
            manager.getVolunteers().addAll(savedVolunteers);
        }
    }

    /**
     * Makes a coordinator with the given volunteer manager.
     * @param manager Manager used for assignments
     * @return Test coordinator
     */
    private Coordinator coordinator(VolunteerManager manager) {
        return new Coordinator(CoordinatorRole.AID, "Alice", "Smith",
            "alice@example.com", "843-555-0100", "asmith", "demo123", manager);
    }

    /**
     * Makes a volunteer with the given availability and verification.
     * @param available Whether the volunteer is available
     * @param verified Whether the volunteer is verified
     * @param dispatched Whether the volunteer is already dispatched
     * @param location Location of the volunteer
     * @return Test volunteer
     */
    private Volunteer volunteer(boolean available, boolean verified, boolean dispatched, Location location) {
        return new Volunteer("Rosa", "Delgado", UUID.randomUUID(),
            "rosa@example.com", "843-555-0171", location, LocalDate.of(1984, 6, 3),
            "rosadelgado", "demo123", NotificationType.SMS, new ArrayList<String>(),
            available, 25.0, skills, credentials, new ArrayList<String>(),
            verified, dispatched, 0.0);
    }

    /**
     * Makes a request with the given assistance type and status.
     * @param type Assistance needed
     * @param status Current request status
     * @return Test request
     */
    private HelpRequest request(AssistanceType type, RequestStatus status) {
        return new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, null,
            "Need transportation", type, Urgency.HIGH_PRIORITY, status, location, "");
    }

    /**
     * Tests that all five volunteers are loaded from the test data file.
     */
    @Test
    public void loadingVolunteerFixtureReturnsAllFiveVolunteers() {
        assertEquals(5, DataLoader.getVolunteers().size());
    }

    /**
     * Tests that an approved certification returns true.
     */
    @Test
    public void approvedCertificationReturnsTrue() {
        assertTrue(coordinator.verifyCertification(volunteer, true));
    }

    /**
     * Tests that a rejected certification returns false.
     */
    @Test
    public void rejectedCertificationReturnsFalse() {
        assertFalse(coordinator.verifyCertification(volunteer, false));
    }

    /**
     * Tests that a missing volunteer cannot have a certification approved.
     */
    @Test
    public void nullVolunteerCannotHaveCertificationApproved() {
        assertFalse(coordinator.verifyCertification(null, true));
    }

    /**
     * Tests that a passed background check returns true.
     */
    @Test
    public void passedBackgroundCheckReturnsTrue() {
        assertTrue(coordinator.runBackgroundCheck(volunteer, true));
    }

    /**
     * Tests that a failed background check returns false.
     */
    @Test
    public void failedBackgroundCheckReturnsFalse() {
        assertFalse(coordinator.runBackgroundCheck(volunteer, false));
    }

    /**
     * Tests that a missing volunteer cannot pass a background check.
     */
    @Test
    public void nullVolunteerCannotPassBackgroundCheck() {
        assertFalse(coordinator.runBackgroundCheck(null, true));
    }

    /**
     * Tests that the coordinator can change the request's urgency.
     */
    @Test
    public void settingUrgencyUpdatesRequest() {
        coordinator.setUrgency(request, Urgency.URGENT_PRIORITY);
        assertEquals(Urgency.URGENT_PRIORITY, request.getUrgency());
    }

    /**
     * Tests that changing urgency requires a request.
     */
    @Test(expected = IllegalArgumentException.class)
    public void settingUrgencyWithoutRequestThrowsException() {
        coordinator.setUrgency(null, Urgency.HIGH_PRIORITY);
    }

    /**
     * Tests that changing urgency requires an urgency value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void settingNullUrgencyThrowsException() {
        coordinator.setUrgency(request, null);
    }

    /**
     * Tests that a qualified volunteer can be assigned to an approved request.
     */
    @Test
    public void assigningQualifiedVolunteerToApprovedRequestWorks() {
        request.setRequestStatus(RequestStatus.APPROVED);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(volunteer.getUserId(), request.getAssignedVolunteerID());
    }

    /**
     * Tests that a qualified volunteer can be assigned to a waiting request.
     */
    @Test
    public void assigningQualifiedVolunteerToWaitingRequestWorks() {
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(volunteer.getUserId(), request.getAssignedVolunteerID());
    }

    /**
     * Tests that a valid EMT credential qualifies for medical assistance.
     */
    @Test
    public void medicalRequestAcceptsValidEmt() {
        request = request(AssistanceType.MEDICAL, RequestStatus.AWAITING_ASSIGNMENT);
        credentials.add(new Credential(CredentialType.EMT, "valid"));
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that a valid nurse credential qualifies for a child injury request.
     */
    @Test
    public void childInjuryRequestAcceptsValidNurse() {
        request = request(AssistanceType.CHILD_INJURY, RequestStatus.AWAITING_ASSIGNMENT);
        credentials.add(new Credential(CredentialType.NURSE, "Valid"));
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that a rescue credential qualifies for a rescue request.
     */
    @Test
    public void rescueRequestAcceptsSearchAndRescueCredential() {
        request = request(AssistanceType.RESCUE, RequestStatus.AWAITING_ASSIGNMENT);
        credentials.add(new Credential(CredentialType.SEARCH_AND_RESCUE, "Valid"));
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that a firefighter credential qualifies for a flood request.
     */
    @Test
    public void floodRequestAcceptsFirefighterCredential() {
        request = request(AssistanceType.FLOOD, RequestStatus.AWAITING_ASSIGNMENT);
        credentials.add(new Credential(CredentialType.FIREFIGHTER, "Valid"));
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that a matching skill ignores capitalization and extra spaces.
     */
    @Test
    public void foodRequestAcceptsFoodDistributionSkill() {
        request = request(AssistanceType.FOOD_SUPPLY, RequestStatus.AWAITING_ASSIGNMENT);
        skills.clear();
        skills.add(" food distribution ");
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that shelter assistance qualifies for a shelter request.
     */
    @Test
    public void shelterRequestAcceptsShelterAssistanceSkill() {
        request = request(AssistanceType.SHELTER, RequestStatus.AWAITING_ASSIGNMENT);
        skills.clear();
        skills.add("Shelter Assistance");
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that construction qualifies for a property damage request.
     */
    @Test
    public void propertyDamageRequestAcceptsConstructionSkill() {
        request = request(AssistanceType.PROPERTY_DAMAGE, RequestStatus.AWAITING_ASSIGNMENT);
        skills.clear();
        skills.add("Construction");
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that debris removal qualifies for a property damage request.
     */
    @Test
    public void propertyDamageRequestAcceptsDebrisRemovalSkill() {
        request = request(AssistanceType.PROPERTY_DAMAGE, RequestStatus.AWAITING_ASSIGNMENT);
        skills.clear();
        skills.add("Debris Removal");
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that medical supply delivery qualifies for a supplies request.
     */
    @Test
    public void medicalSuppliesRequestAcceptsDeliverySkill() {
        request = request(AssistanceType.MEDICAL_SUPPLIES, RequestStatus.AWAITING_ASSIGNMENT);
        skills.clear();
        skills.add("Medical Supply Delivery");
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that animal care qualifies for a pet injury request.
     */
    @Test
    public void petInjuryRequestAcceptsAnimalCareSkill() {
        request = request(AssistanceType.PET_INJURY, RequestStatus.AWAITING_ASSIGNMENT);
        skills.clear();
        skills.add("Animal Care");
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.ASSIGNED, request.getRequestStatus());
    }

    /**
     * Tests that a missing volunteer cannot be assigned.
     */
    @Test
    public void assigningNullVolunteerKeepsRequestWaiting() {
        coordinator.assignVolunteer(null, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that a missing request cannot dispatch a volunteer.
     */
    @Test
    public void assigningWithoutRequestKeepsVolunteerUndispatched() {
        coordinator.assignVolunteer(volunteer, null);
        assertFalse(volunteer.isDispatched());
    }

    /**
     * Tests that assignment requires a volunteer manager.
     */
    @Test
    public void assigningWithoutManagerKeepsRequestWaiting() {
        coordinator = coordinator(null);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that a pending review request is not assigned.
     */
    @Test
    public void assigningPendingRequestIsRejected() {
        request.setRequestStatus(RequestStatus.PENDING_REVIEW);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.PENDING_REVIEW, request.getRequestStatus());
    }

    /**
     * Tests that an unavailable volunteer is not assigned.
     */
    @Test
    public void assigningUnavailableVolunteerIsRejected() {
        volunteer = volunteer(false, true, false, location);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that an unverified volunteer is not assigned.
     */
    @Test
    public void assigningUnverifiedVolunteerIsRejected() {
        volunteer = volunteer(true, false, false, location);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that an already dispatched volunteer is not assigned.
     */
    @Test
    public void assigningDispatchedVolunteerIsRejected() {
        volunteer = volunteer(true, true, true, location);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that an unrelated skill does not qualify for transportation.
     */
    @Test
    public void assigningVolunteerWithoutMatchingSkillIsRejected() {
        skills.clear();
        skills.add("Food Distribution");
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that an expired credential does not qualify for medical assistance.
     */
    @Test
    public void assigningExpiredMedicalCredentialIsRejected() {
        request = request(AssistanceType.MEDICAL, RequestStatus.AWAITING_ASSIGNMENT);
        credentials.add(new Credential(CredentialType.EMT, "Expired"));
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that a missing skill list does not qualify for transportation.
     */
    @Test
    public void assigningVolunteerWithNullSkillsIsRejected() {
        skills = null;
        volunteer = volunteer(true, true, false, location);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that a missing credential list does not qualify for medical assistance.
     */
    @Test
    public void assigningVolunteerWithNullCredentialsIsRejected() {
        credentials = null;
        volunteer = volunteer(true, true, false, location);
        request = request(AssistanceType.MEDICAL, RequestStatus.AWAITING_ASSIGNMENT);
        coordinator.assignVolunteer(volunteer, request);
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT, request.getRequestStatus());
    }

    /**
     * Tests that automatic matching rejects a missing request.
     */
    @Test
    public void automaticAssignmentRejectsNullRequest() throws Exception {
        assertNull(coordinator.autoAssignVolunteer(null));
    }

    /**
     * Tests that automatic matching rejects a missing request location.
     */
    @Test
    public void automaticAssignmentRejectsMissingLocation() throws Exception {
        request = new HelpRequest(UUID.randomUUID(), RequestType.PERSONAL, null,
            "Need transportation", AssistanceType.TRANSPORT, Urgency.HIGH_PRIORITY,
            RequestStatus.AWAITING_ASSIGNMENT, null, "");
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching rejects a missing assistance type.
     */
    @Test
    public void automaticAssignmentRejectsMissingAssistanceType() throws Exception {
        request = request(null, RequestStatus.AWAITING_ASSIGNMENT);
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching rejects a request awaiting review.
     */
    @Test
    public void automaticAssignmentRejectsPendingRequest() throws Exception {
        request.setRequestStatus(RequestStatus.PENDING_REVIEW);
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching requires a volunteer manager.
     */
    @Test
    public void automaticAssignmentRejectsMissingManager() throws Exception {
        coordinator = coordinator(null);
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that no match is returned when the volunteer list is empty.
     */
    @Test
    public void automaticAssignmentReturnsNullForEmptyVolunteerList() throws Exception {
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching skips a null volunteer entry.
     */
    @Test
    public void automaticAssignmentSkipsNullVolunteer() throws Exception {
        manager.getVolunteers().add(null);
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching skips a volunteer without a location.
     */
    @Test
    public void automaticAssignmentSkipsVolunteerWithoutLocation() throws Exception {
        manager.addVolunteer(volunteer(true, true, false, null));
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching skips unavailable volunteers.
     */
    @Test
    public void automaticAssignmentSkipsUnavailableVolunteer() throws Exception {
        manager.addVolunteer(volunteer(false, true, false, location));
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching skips unverified volunteers.
     */
    @Test
    public void automaticAssignmentSkipsUnverifiedVolunteer() throws Exception {
        manager.addVolunteer(volunteer(true, false, false, location));
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching skips dispatched volunteers.
     */
    @Test
    public void automaticAssignmentSkipsDispatchedVolunteer() throws Exception {
        manager.addVolunteer(volunteer(true, true, true, location));
        assertNull(coordinator.autoAssignVolunteer(request));
    }

    /**
     * Tests that automatic matching skips volunteers with unrelated skills.
     */
    @Test
    public void automaticAssignmentSkipsUnqualifiedVolunteer() throws Exception {
        skills.clear();
        skills.add("Animal Care");
        manager.addVolunteer(volunteer);
        assertNull(coordinator.autoAssignVolunteer(request));
    }
}
