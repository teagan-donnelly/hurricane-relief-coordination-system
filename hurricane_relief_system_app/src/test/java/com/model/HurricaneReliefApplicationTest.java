package com.model;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Gauvin
 * Tests for the behavior of the {@link HurricaneReliefApplication} facade.
 *
 *
 * +-------------------------------------------------------+----------------------------------------------------------------+
 * | Test                                                  | Reasoning                                                      |
 * +-------------------------------------------------------+----------------------------------------------------------------+
 * | getInstanceReturnsSameApplication                     | Verifies that our instance is actually a singleton             |
 * | submittingHelpRequestAddsItToRequestManager           | Submitting a help request must add it to the manager.          |
 * | submittingHelpRequestWithoutUserIsNotAdded            | A help request submitted without a user must not be added.     |
 * | submittedRequestStatusCanBeTracked                    | The status of a newly submitted request must be trackable.     |
 * | findingVolunteersReturnsOnlyAvailableVolunteers       | Finding volunteers must return only those who are available.   |
 * | findingVolunteersLeavesOutUnavailableVolunteers       | Finding volunteers must exclude any unavailble volunteers.     |
 * | findingVolunteersLeavesOutDispatchedVolunteers        | Finding volunteers must exclude any occupied volunteers.       |
 * | findingVolunteersLeavesOutUnverifiedVolunteers        | Finding volunteers must exclude any unverified volunteers.     |
 * | findingSheltersReturnsOnlyOpenShelters                | Finding shelters must return only open shelters.               |
 * | findingSheltersLeavesOutFullShelters                  | Finding shelters must exclude any full shelters.               |
 * | registeredVolunteerCanLogIn                           | A volunteer must be able to log in with their credentials.     |
 * | registeringDuplicateUsernameIsRejected                | Registering with an already taken username must be rejected.   |
 * | registeringShelterWithNegativeCapacityIsRejected      | Users cannot rejester a shelter with negative capacity.        |
 * | viewingShelterShowsShelterInformation                 | Viewing a shelter must display its information.                |
 * | editingShelterBelowZeroOccupancyIsRejected            | Editing a shelter to have negative occupancy is not allowed.   |
 * | editingUnknownShelterChangesNothing                   | Editing a shelter that does not exist must change nothing.     |
 * | requestStatusIsFoundById                              | The status of an existing request must be found by its ID.     |
 * | requestStatusForUnknownIdIsNull                       | Looking up the status for an unknown ID must return null.      |
 * | correctCredentialsLogIn                               | Entering correct username and password must log in.            |
 * | wrongPasswordDoesNotLogIn                             | Providing an incorrect password must fail to log in.           |
 * | unknownUsernameDoesNotLogIn                           | Providing an unknown username must fail to log in.             |
 * | loggingOutAfterLoginSucceeds                          | Logging out after a login must succeed and confirm it.         |
 * +-------------------------------------------------------+----------------------------------------------------------------+
 */
public class HurricaneReliefApplicationTest {

    private static final UUID BURKE_ID = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final UUID TRIDENT_ID = UUID.fromString("22222222-2222-4222-8222-222222222222");
    private static final UUID FELIX_ID = UUID.fromString("e749d25b-831a-4c60-a219-75b3f9d08642");
    private static final UUID SAMUEL_ID = UUID.fromString("9b473d60-a815-42e9-967c-3f04b2d18a75");

    private HurricaneReliefApplication app;
    private byte[] originalShelterFile;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;
    private Location charleston;
    private ArrayList<UUID> submittedRequests = new ArrayList<UUID>();

    /**
     * Saves a copy of the shelter test file, replaces it with two known shelters,
     * makes sure nobody is logged in, and starts catching printed output.
     */
    @Before
    public void setUp() throws Exception {
        originalShelterFile = Files.readAllBytes(shelterFile().toPath());
        Files.write(shelterFile().toPath(), ("["
            + shelterJSON("Burke High School", BURKE_ID, FELIX_ID, 10, 100, "OPEN") + ","
            + shelterJSON("Trident Tech Gym", TRIDENT_ID, SAMUEL_ID, 50, 50, "FULL")
            + "]").getBytes());

        charleston = new Location(32.7920, -79.9450, "244 President St", 0, "Charleston", "SC", "29403");
        app = HurricaneReliefApplication.getInstance();
        app.logout();

        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    /**
     * Puts the shelter test file back, removes any requests the test added, and logs out.
     */
    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
        Files.write(shelterFile().toPath(), originalShelterFile);
        for (UUID id : submittedRequests) {
            RequestManager.getInstance().removeRequest(RequestManager.getInstance().getRequest(id));
        }
        app.logout();
    }

    /**
     * Finds the test copy of the shelter file.
     * @return The shelter file
     */
    private File shelterFile() throws Exception {
        return new File(HurricaneReliefApplicationTest.class.getResource("resources/data/shelter.json").toURI());
    }

    /**
     * Makes the JSON text for one test shelter.
     * @param name Name of the shelter
     * @param id ID of the shelter
     * @param admin ID of the shelter manager
     * @param occupancy Number of people in the shelter
     * @param capacity Max number of people
     * @param status Status of the shelter
     * @return JSON text of the shelter
     */
    private String shelterJSON(String name, UUID id, UUID admin, int occupancy, int capacity, String status) {
        return "{\"name\":\"" + name + "\",\"id\":\"" + id + "\",\"admin\":\"" + admin + "\","
            + "\"location\":{\"latitude\":32.792,\"longitude\":-79.945,\"streetAddress\":\"244 President St\","
            + "\"apartmentNum\":0,\"city\":\"Charleston\",\"state\":\"SC\",\"zipcode\":\"29403\"},"
            + "\"currentOccupancy\":" + occupancy + ",\"capacity\":" + capacity + ",\"petFriendly\":true,"
            + "\"accessibilityInfo\":[],\"hasPower\":true,\"status\":\"" + status + "\",\"hasMedicalStaff\":true,"
            + "\"petCapacity\":5,\"petOccupancy\":0,\"occupants\":[],\"pets\":[]}";
    }

    /**
     * Makes a test aid requester.
     * @return Test requester
     */
    private AidRequester carlos() {
        return new AidRequester("Carlos", "Mendoza", UUID.fromString("f9e8d7c6-b5a4-3210-9876-543210fedcba"),
            "carlosm@example.com", "912-555-0102", charleston, LocalDate.of(1995, 11, 23), "cmendoza",
            "hashed_password_456", NotificationType.SMS, new ArrayList<String>(), 2, AssistanceType.MEDICAL,
            new ArrayList<String>());
    }

    /**
     * Makes an example volunteer that gives registerUser a username and password.
     * @param username Login name
     * @param password Login password
     * @return Example volunteer
     */
    private Volunteer volunteerType(String username, String password) {
        return new Volunteer("Rosa", "Martinez", UUID.randomUUID(), "rosa@example.com", "843-555-0182",
            charleston, LocalDate.of(1990, 6, 15), username, password, NotificationType.EMAIL,
            new ArrayList<String>(), true, 25.0, new ArrayList<String>(), new ArrayList<Credential>(),
            new ArrayList<String>(), false, false, 0.0);
    }

    /**
     * Registers a new account using the given example user.
     * @param type Example user with the account type and login credentials
     */
    private void register(User type) {
        app.registerUser(type, "Rosa", "Martinez", "rosa@example.com", charleston, LocalDate.of(1990, 6, 15),
            true, 25.0, new ArrayList<String>(), new ArrayList<Credential>(), new ArrayList<String>(),
            false, false, 0.0, 1, AssistanceType.TRANSPORT, new ArrayList<String>());
    }

    /**
     * Submits a transport request from Carlos and remembers it so it can be removed later.
     * @param user User making the request
     * @return ID of the request
     */
    private UUID submitRequest(User user) {
        UUID id = UUID.randomUUID();
        submittedRequests.add(id);
        app.submitHelpRequest(id, RequestType.PERSONAL, user, "Need a ride to a shelter", AssistanceType.TRANSPORT,
            Urgency.HIGH_PRIORITY, RequestStatus.PENDING_REVIEW, charleston, "");
        return id;
    }

    /**
     * Registers a test shelter called Hampton Park Rec Center.
     * @param capacity Max number of people
     */
    private void registerHamptonPark(int capacity) {
        app.registerShelter("Hampton Park Rec Center", UUID.fromString("33333333-3333-4333-8333-333333333333"),
            charleston, 0, capacity, true, new ArrayList<String>(), true, ShelterStatus.OPEN, false, FELIX_ID);
    }

    /**
     * Gets the current number of people in a shelter from the shelter file.
     * @param id ID of the shelter
     * @return Number of people in the shelter
     */
    private int occupancyOf(UUID id) {
        return new ShelterManager().findShelter(id).getCurrentOccupancy();
    }

    /**
     * Tests that getInstance always gives back the same application.
     */
    @Test
    public void getInstanceReturnsSameApplication() {
        assertSame(app, HurricaneReliefApplication.getInstance());
    }

    /**
     * Tests that a submitted request is stored in the request manager.
     */
    @Test
    public void submittingHelpRequestAddsItToRequestManager() {
        UUID id = submitRequest(carlos());
        assertNotNull(RequestManager.getInstance().getRequest(id));
    }

    /**
     * Tests that a request with no user is not stored.
     */
    @Test
    public void submittingHelpRequestWithoutUserIsNotAdded() {
        UUID id = submitRequest(null);
        assertNull(RequestManager.getInstance().getRequest(id));
    }

    /**
     * Tests that the status of a request that was just submitted can be looked up.
     */
    @Test
    public void submittedRequestStatusCanBeTracked() {
        UUID id = submitRequest(carlos());
        assertEquals(RequestStatus.PENDING_REVIEW, app.getRequestStatus(id));
    }

    /**
     * Tests that the search returns the available volunteers from the test data.
     */
    @Test
    public void findingVolunteersReturnsOnlyAvailableVolunteers() {
        assertEquals(3, app.findAvailableVolunteers().size());
    }

    /**
     * Tests that a volunteer who marked themselves unavailable is left out.
     */
    @Test
    public void findingVolunteersLeavesOutUnavailableVolunteers() {
        for (Volunteer volunteer : app.findAvailableVolunteers()) {
            assertNotEquals("vramirez75", volunteer.getUserName());
        }
    }

    /**
     * Tests that a volunteer who is already out on a job is left out.
     */
    @Test
    public void findingVolunteersLeavesOutDispatchedVolunteers() {
        for (Volunteer volunteer : app.findAvailableVolunteers()) {
            assertFalse(volunteer.isDispatched());
        }
    }

    /**
     * Tests that a volunteer who has not been verified is left out.
     */
    @Test
    public void findingVolunteersLeavesOutUnverifiedVolunteers() {
        for (Volunteer volunteer : app.findAvailableVolunteers()) {
            assertTrue(volunteer.isVerified());
        }
    }

    /**
     * Tests that only the open shelter is returned.
     */
    @Test
    public void findingSheltersReturnsOnlyOpenShelters() {
        List<Shelter> shelters = app.findShelters();
        assertEquals(1, shelters.size());
    }

    /**
     * Tests that a full shelter is left out.
     */
    @Test
    public void findingSheltersLeavesOutFullShelters() {
        for (Shelter shelter : app.findShelters()) {
            assertNotEquals(TRIDENT_ID, shelter.getId());
        }
    }

    /**
     * Tests that a newly registered volunteer can log in.
     */
    @Test
    public void registeredVolunteerCanLogIn() {
        register(volunteerType("rosamartinez", "volunteer123"));
        assertTrue(app.login("rosamartinez", "volunteer123"));
    }

    /**
     * Tests that a second account can not be made with a username that is already taken.
     */
    @Test
    public void registeringDuplicateUsernameIsRejected() {
        register(volunteerType("freed87", "stolenpass1"));
        assertFalse(app.login("freed87", "stolenpass1"));
    }

    /**
     * Tests that a shelter with a negative capacity is not added.
     */
    @Test
    public void registeringShelterWithNegativeCapacityIsRejected() {
        app.login("freed87", "password");
        registerHamptonPark(-10);
        assertEquals(2, new ShelterManager().getShelters().size());
    }

    /**
     * Tests that viewing a shelter shows its information to the user.
     */
    @Test
    public void viewingShelterShowsShelterInformation() {
        app.login("freed87", "password");
        app.viewShelter(BURKE_ID);
        assertTrue(output.toString().contains("Burke High School"));
    }

    /**
     * Tests that a shelter can not end up with fewer than zero people in it.
     */
    @Test
    public void editingShelterBelowZeroOccupancyIsRejected() {
        app.login("freed87", "password");
        app.editShelter(BURKE_ID, -50, true, 0, true, ShelterStatus.OPEN, true);
        assertEquals(10, occupancyOf(BURKE_ID));
    }

    /**
     * Tests that editing a shelter that does not exist leaves the real shelters alone.
     */
    @Test
    public void editingUnknownShelterChangesNothing() {
        app.login("freed87", "password");
        app.editShelter(UUID.fromString("99999999-9999-4999-8999-999999999999"), 5, true, 0, true,
            ShelterStatus.OPEN, true);
        assertEquals(10, occupancyOf(BURKE_ID));
    }

    /**
     * Tests that the status of a request in the test data can be found.
     */
    @Test
    public void requestStatusIsFoundById() {
        assertEquals(RequestStatus.AWAITING_ASSIGNMENT,
            app.getRequestStatus(UUID.fromString("139a95f0-c8a5-4d66-a8c8-b737f99ad142")));
    }

    /**
     * Tests that an unknown request ID gives back no status.
     */
    @Test
    public void requestStatusForUnknownIdIsNull() {
        assertNull(app.getRequestStatus(UUID.fromString("99999999-9999-4999-8999-999999999999")));
    }

    /**
     * Tests that the correct username and password log in.
     */
    @Test
    public void correctCredentialsLogIn() {
        assertTrue(app.login("jdoe88", "hashed_password_123"));
    }

    /**
     * Tests that the wrong password does not log in.
     */
    @Test
    public void wrongPasswordDoesNotLogIn() {
        assertFalse(app.login("jdoe88", "wrongpassword"));
    }

    /**
     * Tests that a username nobody has does not log in.
     */
    @Test
    public void unknownUsernameDoesNotLogIn() {
        assertFalse(app.login("nobody123", "password"));
    }

    /**
     * Tests that logging out after logging in tells the user it worked.
     */
    @Test
    public void loggingOutAfterLoginSucceeds() {
        app.login("jdoe88", "hashed_password_123");
        app.logout();
        assertTrue(output.toString().contains("Logout successful."));
    }
}
