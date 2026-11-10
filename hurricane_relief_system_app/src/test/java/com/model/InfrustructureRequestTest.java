package com.model;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Gauvin
 * Tests for the behavior of an {@link InfrustructureRequest}.
 *
 *
 * +----------------------------------------------------+----------------------------------------------------------------------------------+
 * | Test                                               | Reasoning                                                                        |
 * +----------------------------------------------------+----------------------------------------------------------------------------------+
 * | constructorStoresHazardType                        | The full constructor must successfully store the hazard type.                    |
 * | constructorStoresReportTime                        | The full constructor must successfully store the report time.                    |
 * | shortConstructorStoresHazardType                   | The short constructor must successfully store the hazard type.                   |
 * | shortConstructorStoresReportTime                   | The short constructor must successfully store the report time.                   |
 * | personalTypeIsNotAllowedOnInfrastructureRequest    | An infrastructure request cannot have its type overridden as a personal request. |
 * | creatingRequestWithoutHazardTypeIsRejected         | Creating an infrastructure request without a hazard type must be rejected.       |
 * | creatingRequestWithFutureReportTimeIsRejected      | Creating a request with a report time in the future must be rejected.            |
 * | savedRequestKeepsHazardType                        | A saved infrastructure request must retain its hazard type after reloading.      |
 * | savedRequestKeepsReportTime                        | A saved infrastructure request must retain its report time after reloading.      |
 * | savedRequestFromShortConstructorCanBeLoaded        | A request created using the short constructor must save and load back.           |
 * +----------------------------------------------------+----------------------------------------------------------------------------------+
 */
public class InfrustructureRequestTest {

    private static final UUID TEST_DATA_REQUEST_ID = UUID.fromString("0f48612a-ff38-4cb3-a2d4-7770890c31d8");

    private byte[] originalRequestFile;
    private AidRequester jane;
    private Location meetingStreet;
    private LocalDateTime reportTime;

    /**
     * Saves a copy of the request test file and makes the test requester, location, and time.
     */
    @Before
    public void setUp() throws Exception {
        originalRequestFile = Files.readAllBytes(requestFile().toPath());
        meetingStreet = new Location(32.7801, -79.9352, "Meeting St & Calhoun St", 0, "Charleston", "SC", "29401");
        jane = new AidRequester("Jane", "Doe", UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"),
            "jane.doe@example.com", "843-555-0101", meetingStreet, LocalDate.of(1988, 4, 12), "jdoe88",
            "hashed_password_123", NotificationType.EMAIL, new ArrayList<String>(), 4, AssistanceType.SHELTER,
            new ArrayList<String>());
        reportTime = LocalDateTime.of(2026, 9, 24, 14, 30);
    }

    /**
     * Puts the request test file back the way it was.
     */
    @After
    public void tearDown() throws Exception {
        Files.write(requestFile().toPath(), originalRequestFile);
    }

    /**
     * Finds the test copy of the help request file.
     * @return The help request file
     */
    private File requestFile() throws Exception {
        return new File(InfrustructureRequestTest.class.getResource("resources/data/aid-request.json").toURI());
    }

    /**
     * Makes a test request about a downed power line using the full constructor.
     * @param type Request type passed to the constructor
     * @param hazard Type of hazard
     * @param time When the hazard was reported
     * @return Test request
     */
    private InfrustructureRequest powerLineRequest(RequestType type, HazardType hazard, LocalDateTime time) {
        return new InfrustructureRequest(UUID.randomUUID(), type, jane, "Power line down across the road",
            AssistanceType.PROPERTY_DAMAGE, Urgency.URGENT_PRIORITY, RequestStatus.PENDING_REVIEW,
            meetingStreet, "line.jpg", hazard, time);
    }

    /**
     * Makes a test request about a downed power line using the short constructor.
     * @return Test request
     */
    private InfrustructureRequest shortPowerLineRequest() {
        return new InfrustructureRequest(UUID.randomUUID(), jane, AssistanceType.PROPERTY_DAMAGE,
            Urgency.URGENT_PRIORITY, RequestStatus.PENDING_REVIEW, meetingStreet,
            HazardType.ELECTRICAL_HAZARD, reportTime);
    }

    /**
     * Finds a request in the request file by its ID.
     * @param id ID of the request
     * @return The loaded request, or null if it was not found
     */
    private HelpRequest loadRequest(UUID id) {
        for (HelpRequest request : DataLoader.getRequests()) {
            if (request.getRequestID().equals(id)) {
                return request;
            }
        }
        return null;
    }

    /**
     * Tests that the full constructor stores the hazard type.
     */
    @Test
    public void constructorStoresHazardType() {
        InfrustructureRequest request = powerLineRequest(RequestType.INFRASTRUCTURE, HazardType.ELECTRICAL_HAZARD, reportTime);
        assertEquals(HazardType.ELECTRICAL_HAZARD, request.getHazardType());
    }

    /**
     * Tests that the full constructor stores the report time.
     */
    @Test
    public void constructorStoresReportTime() {
        InfrustructureRequest request = powerLineRequest(RequestType.INFRASTRUCTURE, HazardType.ELECTRICAL_HAZARD, reportTime);
        assertEquals(reportTime, request.getReportTime());
    }

    /**
     * Tests that the short constructor stores the hazard type.
     */
    @Test
    public void shortConstructorStoresHazardType() {
        assertEquals(HazardType.ELECTRICAL_HAZARD, shortPowerLineRequest().getHazardType());
    }

    /**
     * Tests that the short constructor stores the report time.
     */
    @Test
    public void shortConstructorStoresReportTime() {
        assertEquals(reportTime, shortPowerLineRequest().getReportTime());
    }

    /**
     * Tests that an infrastructure request can not be labeled as a personal request.
     */
    @Test
    public void personalTypeIsNotAllowedOnInfrastructureRequest() {
        InfrustructureRequest request = powerLineRequest(RequestType.PERSONAL, HazardType.ELECTRICAL_HAZARD, reportTime);
        assertEquals(RequestType.INFRASTRUCTURE, request.getRequestType());
    }

    /**
     * Tests that an infrastructure request must say what kind of hazard it is.
     */
    @Test(expected = IllegalArgumentException.class)
    public void creatingRequestWithoutHazardTypeIsRejected() {
        powerLineRequest(RequestType.INFRASTRUCTURE, null, reportTime);
    }

    /**
     * Tests that a hazard can not be reported at a time that has not happened yet.
     */
    @Test(expected = IllegalArgumentException.class)
    public void creatingRequestWithFutureReportTimeIsRejected() {
        powerLineRequest(RequestType.INFRASTRUCTURE, HazardType.ELECTRICAL_HAZARD, LocalDateTime.now().plusDays(3));
    }

    /**
     * Tests that a saved infrastructure request keeps its hazard type.
     */
    @Test
    public void savedRequestKeepsHazardType() {
        InfrustructureRequest request = powerLineRequest(RequestType.INFRASTRUCTURE, HazardType.ROAD_HAZARD, reportTime);
        DataWriter.saveRequest(request);
        InfrustructureRequest loaded = (InfrustructureRequest) loadRequest(request.getRequestID());
        assertEquals(HazardType.ROAD_HAZARD, loaded.getHazardType());
    }

    /**
     * Tests that a saved infrastructure request keeps its report time.
     */
    @Test
    public void savedRequestKeepsReportTime() {
        InfrustructureRequest request = powerLineRequest(RequestType.INFRASTRUCTURE, HazardType.ROAD_HAZARD, reportTime);
        DataWriter.saveRequest(request);
        InfrustructureRequest loaded = (InfrustructureRequest) loadRequest(request.getRequestID());
        assertEquals(reportTime, loaded.getReportTime());
    }

    /**
     * Tests that a request made with the short constructor can be saved and loaded again.
     */
    @Test
    public void savedRequestFromShortConstructorCanBeLoaded() {
        InfrustructureRequest request = shortPowerLineRequest();
        DataWriter.saveRequest(request);
        assertNotNull(loadRequest(request.getRequestID()));
    }
}