package com.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Nicolas Gauvin
 * @author Jorge Naranjo Jr.
 * Test class to verify the functionality of saving users and requests via {@link DataWriter}.
 *
 * AI disclosure: the tests by Jorge Naranjo Jr. and the reasoning table below were drafted with help from Claude (Anthropic).
 *
 * +-----------------------------------------------------+---------------------------------------------------------------------------------------------------------+
 * | Test                                                | Reasoning                                                                                               |
 * +-----------------------------------------------------+---------------------------------------------------------------------------------------------------------+
 * | testSaveData                                        | Saving users and requests is the most basic job of DataWriter, so both saves should succeed.            |
 * | savingNewRequesterAddsThemToRequesterFile           | A new aid requester has to be saved so they can log back in and track their request.                    |
 * | savingExistingRequesterDoesNotDuplicateThem         | Duplicate copies of a person could make coordinators think two different families need help.            |
 * | resavedRequesterHasUpdatedHouseholdSize             | Household size decides how many seats and shelter beds are needed, so updates must be saved.            |
 * | savingRequesterKeepsOtherRequesters                 | Saving one person should never erase the other families already in the system.                          |
 * | savingRequesterLeavesRequestFileUnchanged           | Writing a person into the help request file would corrupt the list of requests volunteers rely on.      |
 * | savingNewRequestAddsItToRequestFile                 | A help request that isn't saved would be lost if the app closes, and the family would never get help.   |
 * | savingExistingRequestDoesNotDuplicateIt             | Duplicate requests could send two volunteers to the same family while other families wait.              |
 * | resavedRequestHasUpdatedStatus                      | If a canceled request kept its old status, volunteers might still be sent to it.                        |
 * | savedRequestIncludesRequesterID                     | The request must stay linked to the person who made it so coordinators can contact them.                |
 * | savedRequestWithoutUserHasEmptyUserID               | A request with no linked user should still be saved instead of crashing, so the request isn't lost.     |
 * | requestWithMissingDetailsIsSavedWithEmptyValues     | Requests made in a hurry may be missing details, and saving them should still work instead of crashing. |
 * | savedRequestIncludesLocation                        | Volunteers need the exact address and coordinates to find the family.                                   |
 * | savedRequestIncludesAssignedVolunteerAndShelter     | Losing these after a restart would leave a family with no one coming and nowhere to go.                 |
 * | savedPersonalRequestIncludesHouseholdMembersAndPets | The names of family members and pets are needed for pickup and shelter check in.                        |
 * | savedPersonalRequestIncludesMedicalNote             | Losing a medical note could put someone's health at risk during transport.                              |
 * | savedInfrastructureRequestIncludesHazardType        | Crews need to know if a hazard is electrical, water, or a road problem to respond safely.               |
 * | savingNewVolunteerAddsThemToVolunteerFile           | A volunteer who signed up must be saved so coordinators can assign them to requests.                    |
 * | savedVolunteerIncludesCredentials                   | Credentials like EMT decide which volunteers can handle medical requests.                               |
 * | volunteerWithMissingListsIsSavedWithEmptyLists      | A volunteer who hasn't added skills or supplies yet should still be saved without crashing.             |
 * | savingNewCoordinatorAddsThemToCoordinatorFile       | Coordinators must be saved so they can log back in and keep managing requests.                          |
 * | savedCoordinatorIncludesRole                        | The role decides what a coordinator is allowed to do, like completing requests as a shelter manager.    |
 * | savingNewShelterAddsItToShelterFile                 | A new shelter must be saved so families can be sent to it.                                              |
 * | savedShelterIncludesPetSpace                        | Pet space decides whether a family with pets can be placed at the shelter.                              |
 * | shelterWithoutAdminIsSavedWithEmptyAdmin            | A shelter that doesn't have a manager yet should still be saved so it can be used.                      |
 * | savingNullUserListReturnsFalse                      | Bad input should be reported as a failure instead of crashing the app.                                  |
 * | savingUsersPutsCoordinatorInCoordinatorFile         | Each type of user is loaded from its own file, so a coordinator in the wrong file could not log in.     |
 * | savingUsersPutsRequesterInRequesterFile             | An aid requester saved in the wrong file would lose access to their account and requests.               |
 * | savingUsersPutsVolunteerInVolunteerFile             | A volunteer saved in the wrong file would never show up as available to help.                           |
 * | savingNullRequestListReturnsFalse                   | Bad input should be reported as a failure instead of crashing the app.                                  |
 * | savingRequestsWritesEveryRequest                    | Every request in the list must be saved, because one missing request means one family without help.     |
 * | removingRecordDeletesIt                             | Records that should be gone, like a closed account, must actually be removed from the file.             |
 * | removingRecordKeepsOtherRecords                     | Removing one record should never delete anyone else's data.                                             |
 * | removingMissingRecordChangesNothing                 | Trying to remove something that isn't there should be harmless and leave the file alone.                |
 * | removingFromMissingFileReturnsFalse                 | If the file can't be read, the method should report the failure instead of crashing.                    |
 * +-----------------------------------------------------+---------------------------------------------------------------------------------------------------------+
 */
public class DataWriterTest {

    private static final String REQUESTER_FILE = "aid-requestors.json";
    private static final String REQUEST_FILE = "aid-request.json";
    private static final String COORDINATOR_FILE = "coordinators.json";
    private static final String VOLUNTEER_FILE = "volunteer.json";
    private static final String SHELTER_FILE = "shelter.json";
    private static final String[] DATA_FILES = {REQUESTER_FILE, REQUEST_FILE, COORDINATOR_FILE, VOLUNTEER_FILE, SHELTER_FILE};

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private HashMap<String, byte[]> originalData = new HashMap<String, byte[]>();
    private Location carterHome;
    private UUID requesterID;

    /**
     * Saves a copy of the test data files so each test starts with the same data.
     */
    @Before
    public void setUp() throws Exception {
        for (String name : DATA_FILES) {
            originalData.put(name, Files.readAllBytes(dataFile(name).toPath()));
        }
        carterHome = new Location(32.7765, -79.9311, "14 Tradd St", 2, "Charleston", "SC", "29401");
        requesterID = UUID.randomUUID();
    }

    /**
     * Puts the test data files back the way they were.
     */
    @After
    public void tearDown() throws Exception {
        for (String name : DATA_FILES) {
            Files.write(dataFile(name).toPath(), originalData.get(name));
        }
    }

    /**
     * Finds the test copy of a data file. During JUnit tests DataWriter should save to the
     * files in com/model/resources/data instead of the real data files.
     * @param name Name of the data file
     * @return The data file
     */
    private File dataFile(String name) throws Exception {
        return new File(DataWriterTest.class.getResource("resources/data/" + name).toURI());
    }

    /**
     * Reads every record in a JSON file.
     * @param file JSON file to read
     * @return Records in the file
     */
    private JSONArray records(File file) throws Exception {
        try (FileReader reader = new FileReader(file)) {
            return (JSONArray) new JSONParser().parse(reader);
        }
    }

    /**
     * Finds a saved record by its ID.
     * @param name Name of the data file
     * @param idKey Key used to find the ID of the record
     * @param id ID of the record
     * @return The record, or null if it was not found
     */
    private JSONObject record(String name, String idKey, UUID id) throws Exception {
        for (Object record : records(dataFile(name))) {
            if (id.toString().equals(((JSONObject) record).get(idKey))) {
                return (JSONObject) record;
            }
        }
        return null;
    }

    /**
     * Counts how many saved records have an ID.
     * @param name Name of the data file
     * @param idKey Key used to find the ID of the record
     * @param id ID of the record
     * @return Number of records with that ID
     */
    private int count(String name, String idKey, UUID id) throws Exception {
        int count = 0;
        for (Object record : records(dataFile(name))) {
            if (id.toString().equals(((JSONObject) record).get(idKey))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Makes a test aid requester.
     * @param householdSize Number of people in the household
     * @return Test aid requester
     */
    private AidRequester requester(int householdSize) {
        ArrayList<String> contacts = new ArrayList<String>();
        contacts.add("843-555-0199");
        return new AidRequester("Carter", "Smith", requesterID, "carter@example.com", "843-555-0101",
            carterHome, LocalDate.of(1980, 6, 2), "csmith", "pass456", NotificationType.SMS,
            contacts, householdSize, AssistanceType.TRANSPORT, new ArrayList<String>());
    }

    /**
     * Makes a test volunteer who is an EMT.
     * @return Test volunteer
     */
    private Volunteer volunteer() {
        ArrayList<Credential> credentials = new ArrayList<Credential>();
        credentials.add(new Credential(CredentialType.EMT, "VERIFIED"));
        ArrayList<String> supplies = new ArrayList<String>();
        supplies.add("Pickup truck");
        return new Volunteer("Maria", "Lee", UUID.randomUUID(), "maria@example.com", "843-555-0150",
            carterHome, LocalDate.of(1995, 3, 14), "mlee", "pass123", NotificationType.SMS,
            new ArrayList<String>(), true, 25, new ArrayList<String>(), credentials, supplies, true, false, 12.5);
    }

    /**
     * Makes a test shelter coordinator.
     * @return Test coordinator
     */
    private Coordinator coordinator() {
        return new Coordinator(CoordinatorRole.SHELTER, "Alice", "Smith", UUID.randomUUID(), "alice@example.com",
            "843-555-0102", "asmith", "pass123", carterHome, LocalDate.of(1990, 5, 12), NotificationType.EMAIL,
            new ArrayList<String>(), null);
    }

    /**
     * Makes a test shelter with room for pets.
     * @param admin ID of the shelter manager, or null if it has none
     * @return Test shelter
     */
    private Shelter shelter(UUID admin) {
        Location location = new Location(32.7920, -79.9450, "244 President St", 0, "Charleston", "SC", "29403");
        Shelter shelter = admin == null
            ? new Shelter("Burke High School", UUID.randomUUID(), location, 10, 100, true,
                new ArrayList<String>(), true, ShelterStatus.OPEN, true)
            : new Shelter("Burke High School", UUID.randomUUID(), location, 10, 100, true,
                new ArrayList<String>(), true, ShelterStatus.OPEN, true, admin);
        shelter.setPetSpace(8, 3);
        return shelter;
    }

    /**
     * Makes a test request for a family of 3 with a dog.
     * @param user User who made the request
     * @return Test request
     */
    private PersonalRequest personalRequest(User user) {
        PersonalRequest request = new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, user,
            "Need transport to a shelter", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.AWAITING_ASSIGNMENT, carterHome, "street.jpg", 3, "Knock on side door", 1, 1);
        request.addHouseholdMember("Carter Smith");
        request.addHouseholdMember("Ella Smith");
        request.addHouseholdMember("Noah Smith");
        request.addPet("Biscuit");
        return request;
    }

    /**
     * Makes a test request about a downed power line.
     * @return Test request
     */
    private InfrustructureRequest infrastructureRequest() {
        return new InfrustructureRequest(UUID.randomUUID(), RequestType.INFRASTRUCTURE, requester(3),
            "Downed power line across road", AssistanceType.PROPERTY_DAMAGE, Urgency.URGENT_PRIORITY,
            RequestStatus.IN_PROGRESS, carterHome, "line.jpg", HazardType.ELECTRICAL_HAZARD,
            LocalDateTime.of(2026, 9, 28, 9, 15));
    }

    /**
     * Saves one coordinator, one aid requester, and one volunteer together.
     * @param coordinator Coordinator to save
     * @param requester Aid requester to save
     * @param volunteer Volunteer to save
     */
    private void saveAllUsers(Coordinator coordinator, AidRequester requester, Volunteer volunteer) {
        ArrayList<User> users = new ArrayList<User>();
        users.add(coordinator);
        users.add(requester);
        users.add(volunteer);
        assertTrue(DataWriter.saveUsers(users));
    }

    /**
     * Makes a JSON file with two records for the remove tests.
     * @return The JSON file
     */
    private File twoRecordFile() throws Exception {
        File file = folder.newFile("records.json");
        Files.write(file.toPath(), "[{\"id\":\"a\",\"name\":\"First\"},{\"id\":\"b\",\"name\":\"Second\"}]".getBytes());
        return file;
    }

    /**
     * Tests saving user and request data to persistent storage.
     */
    @Test
    public void testSaveData() {
        System.out.println("=== RUNNING DATAWRITER TEST ===");

        Location mockLocation = new Location(33.9988, -81.0348, "123 Main St", 4, "Columbia", "SC", "29201");

        ArrayList<String> contacts = new ArrayList<String>();
        contacts.add("803-555-0199");

        VolunteerManager volunteerManager = null;

        ArrayList<User> testUsers = new ArrayList<User>();

        Coordinator testCoordinator = new Coordinator(CoordinatorRole.AID,
            "Alice", "Smith", UUID.randomUUID(), "alice@example.com",
            "803-555-0101", "asmith", "pass123", mockLocation, LocalDate.of(1990, 5, 12),
            NotificationType.EMAIL, contacts, volunteerManager
        );

        AidRequester testRequester = new AidRequester(
            "Bob", "Jones", UUID.randomUUID(), "bob@example.com", "803-555-0102",
            mockLocation, LocalDate.of(1985, 10, 20), "bjones", "pass456",
            NotificationType.SMS, contacts, 4, AssistanceType.FOOD_SUPPLY, new ArrayList<String>()
        );

        testUsers.add(testCoordinator);
        testUsers.add(testRequester);

        boolean usersSaved = DataWriter.saveUsers(testUsers);
        System.out.println("DataWriter.saveUsers success: " + usersSaved);
        assertTrue("DataWriter.saveUsers should return true on success.", usersSaved);

        ArrayList<HelpRequest> testRequests = new ArrayList<HelpRequest>();

        PersonalRequest personalReq = new PersonalRequest(
            UUID.randomUUID(), RequestType.PERSONAL, testRequester, "Need fresh water",
            AssistanceType.FOOD_SUPPLY, Urgency.HIGH_PRIORITY, RequestStatus.PENDING_REVIEW, mockLocation,
            "photo_water.jpg", 4, "Knock on side door", 0, 0
        );

        InfrustructureRequest infraReq = new InfrustructureRequest(
            UUID.randomUUID(), RequestType.INFRASTRUCTURE, testRequester, "Downed power line across road",
            AssistanceType.PROPERTY_DAMAGE, Urgency.URGENT_PRIORITY, RequestStatus.IN_PROGRESS, mockLocation,
            "photo_line.jpg", HazardType.ELECTRICAL_HAZARD, LocalDateTime.now()
        );

        testRequests.add(personalReq);
        testRequests.add(infraReq);

        boolean requestsSaved = DataWriter.saveRequests(testRequests);
        System.out.println("DataWriter.saveRequests success: " + requestsSaved);
        assertTrue("DataWriter.saveRequests should return true on success.", requestsSaved);
    }

    /**
     * Tests that a new aid requester is added to the requester file.
     */
    @Test
    public void savingNewRequesterAddsThemToRequesterFile() throws Exception {
        assertTrue(DataWriter.saveRequester(requester(3)));
        assertNotNull(record(REQUESTER_FILE, AppConstants.USER_ID, requesterID));
    }

    /**
     * Tests that saving a requester who is already saved does not make a second copy.
     */
    @Test
    public void savingExistingRequesterDoesNotDuplicateThem() throws Exception {
        DataWriter.saveRequester(requester(3));
        DataWriter.saveRequester(requester(5));
        assertEquals(1, count(REQUESTER_FILE, AppConstants.USER_ID, requesterID));
    }

    /**
     * Tests that saving a requester again replaces their old information.
     */
    @Test
    public void resavedRequesterHasUpdatedHouseholdSize() throws Exception {
        DataWriter.saveRequester(requester(3));
        DataWriter.saveRequester(requester(5));
        JSONObject saved = record(REQUESTER_FILE, AppConstants.USER_ID, requesterID);
        assertNotNull(saved);
        assertEquals(5, ((Number) saved.get(AppConstants.USER_HOUSEHOLDSIZE)).intValue());
    }

    /**
     * Tests that saving one requester keeps everyone else who was already saved.
     */
    @Test
    public void savingRequesterKeepsOtherRequesters() throws Exception {
        int before = records(dataFile(REQUESTER_FILE)).size();
        DataWriter.saveRequester(requester(3));
        assertEquals(before + 1, records(dataFile(REQUESTER_FILE)).size());
    }

    /**
     * Tests that saving a requester does not write into the help request file.
     */
    @Test
    public void savingRequesterLeavesRequestFileUnchanged() throws Exception {
        DataWriter.saveRequester(requester(3));
        assertArrayEquals(originalData.get(REQUEST_FILE), Files.readAllBytes(dataFile(REQUEST_FILE).toPath()));
    }

    /**
     * Tests that a new help request is added to the request file.
     */
    @Test
    public void savingNewRequestAddsItToRequestFile() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        assertTrue(DataWriter.saveRequest(request));
        assertNotNull(record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID()));
    }

    /**
     * Tests that saving a request that is already saved does not make a second copy.
     */
    @Test
    public void savingExistingRequestDoesNotDuplicateIt() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        DataWriter.saveRequest(request);
        request.cancelRequest();
        DataWriter.saveRequest(request);
        assertEquals(1, count(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID()));
    }

    /**
     * Tests that saving a request again replaces its old status.
     */
    @Test
    public void resavedRequestHasUpdatedStatus() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        DataWriter.saveRequest(request);
        request.cancelRequest();
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals("CANCELED", saved.get(AppConstants.REQUEST_STATUS));
    }

    /**
     * Tests that a saved request remembers which requester made it.
     */
    @Test
    public void savedRequestIncludesRequesterID() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals(requesterID.toString(), saved.get(AppConstants.REQUEST_USER_ID));
    }

    /**
     * Tests that a request with no requester is saved with an empty user ID.
     */
    @Test
    public void savedRequestWithoutUserHasEmptyUserID() throws Exception {
        PersonalRequest request = personalRequest(null);
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals("", saved.get(AppConstants.REQUEST_USER_ID));
    }

    /**
     * Tests that a request missing its type, urgency, status, and assistance type is saved with empty values.
     */
    @Test
    public void requestWithMissingDetailsIsSavedWithEmptyValues() throws Exception {
        HelpRequest request = new HelpRequest(UUID.randomUUID(), null, null, null, null, carterHome);
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals("", saved.get(AppConstants.REQUEST_TYPE));
        assertEquals("", saved.get(AppConstants.REQUEST_URGENCY));
        assertEquals("", saved.get(AppConstants.REQUEST_STATUS));
        assertEquals("", saved.get(AppConstants.REQUEST_ASSISTANCE_TYPE));
    }

    /**
     * Tests that a saved request keeps the address where help is needed.
     */
    @Test
    public void savedRequestIncludesLocation() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        JSONObject location = (JSONObject) saved.get(AppConstants.REQUEST_LOCATION);
        assertEquals("14 Tradd St", location.get(AppConstants.LOCATION_STREET_ADDRESS));
        assertEquals(32.7765, ((Number) location.get(AppConstants.LOCATION_LATITUDE)).doubleValue(), 0.0001);
    }

    /**
     * Tests that a saved request keeps its assigned volunteer and shelter.
     */
    @Test
    public void savedRequestIncludesAssignedVolunteerAndShelter() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        UUID volunteerID = UUID.randomUUID();
        UUID shelterID = UUID.randomUUID();
        request.setAssignedVolunteerID(volunteerID);
        request.setSelectedShelterID(shelterID);
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals(volunteerID.toString(), saved.get(AppConstants.REQUEST_ASSIGNED_VOLUNTEER));
        assertEquals(shelterID.toString(), saved.get(AppConstants.REQUEST_SELECTED_SHELTER));
    }

    /**
     * Tests that a saved personal request keeps the names of the family and their pets.
     */
    @Test
    public void savedPersonalRequestIncludesHouseholdMembersAndPets() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals(3, ((JSONArray) saved.get(AppConstants.REQUEST_HOUSEHOLD_MEMBERS)).size());
        assertTrue(((JSONArray) saved.get(AppConstants.REQUEST_PET_NAMES)).contains("Biscuit"));
    }

    /**
     * Tests that a saved personal request keeps its medical note.
     */
    @Test
    public void savedPersonalRequestIncludesMedicalNote() throws Exception {
        PersonalRequest request = personalRequest(requester(3));
        request.setMedicalNote("Noah uses an inhaler for asthma");
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals("Noah uses an inhaler for asthma", saved.get(AppConstants.REQUEST_MEDICAL_NOTE));
    }

    /**
     * Tests that a saved infrastructure request keeps its hazard type.
     */
    @Test
    public void savedInfrastructureRequestIncludesHazardType() throws Exception {
        InfrustructureRequest request = infrastructureRequest();
        DataWriter.saveRequest(request);
        JSONObject saved = record(REQUEST_FILE, AppConstants.REQUEST_ID, request.getRequestID());
        assertNotNull(saved);
        assertEquals("ELECTRICAL_HAZARD", saved.get(AppConstants.REQUEST_HAZARD_TYPE));
    }

    /**
     * Tests that a new volunteer is added to the volunteer file.
     */
    @Test
    public void savingNewVolunteerAddsThemToVolunteerFile() throws Exception {
        Volunteer volunteer = volunteer();
        assertTrue(DataWriter.saveVolunteer(volunteer));
        assertNotNull(record(VOLUNTEER_FILE, AppConstants.USER_ID, volunteer.getUserId()));
    }

    /**
     * Tests that a saved volunteer keeps their credentials.
     */
    @Test
    public void savedVolunteerIncludesCredentials() throws Exception {
        Volunteer volunteer = volunteer();
        DataWriter.saveVolunteer(volunteer);
        JSONObject saved = record(VOLUNTEER_FILE, AppConstants.USER_ID, volunteer.getUserId());
        assertNotNull(saved);
        JSONObject credential = (JSONObject) ((JSONArray) saved.get(AppConstants.USER_CREDENTIALS)).get(0);
        assertEquals("EMT", credential.get(AppConstants.USER_CREDENTIALS_TYPE));
    }

    /**
     * Tests that a volunteer with no skills, credentials, or supplies is saved with empty lists.
     */
    @Test
    public void volunteerWithMissingListsIsSavedWithEmptyLists() throws Exception {
        Volunteer volunteer = new Volunteer(true, null, null);
        DataWriter.saveVolunteer(volunteer);
        JSONObject saved = record(VOLUNTEER_FILE, AppConstants.USER_ID, volunteer.getUserId());
        assertNotNull(saved);
        assertTrue(((JSONArray) saved.get(AppConstants.USER_SKILLS)).isEmpty());
        assertTrue(((JSONArray) saved.get(AppConstants.USER_CREDENTIALS)).isEmpty());
        assertTrue(((JSONArray) saved.get(AppConstants.USER_SUPPLIES)).isEmpty());
    }

    /**
     * Tests that a new coordinator is added to the coordinator file.
     */
    @Test
    public void savingNewCoordinatorAddsThemToCoordinatorFile() throws Exception {
        Coordinator coordinator = coordinator();
        assertTrue(DataWriter.saveCoordinator(coordinator));
        assertNotNull(record(COORDINATOR_FILE, AppConstants.USER_ID, coordinator.getUserId()));
    }

    /**
     * Tests that a saved coordinator keeps their role.
     */
    @Test
    public void savedCoordinatorIncludesRole() throws Exception {
        Coordinator coordinator = coordinator();
        DataWriter.saveCoordinator(coordinator);
        JSONObject saved = record(COORDINATOR_FILE, AppConstants.USER_ID, coordinator.getUserId());
        assertNotNull(saved);
        assertEquals("SHELTER", saved.get(AppConstants.COORDINATOR_ROLE));
    }

    /**
     * Tests that a new shelter is added to the shelter file.
     */
    @Test
    public void savingNewShelterAddsItToShelterFile() throws Exception {
        Shelter shelter = shelter(UUID.randomUUID());
        assertTrue(DataWriter.saveShelter(shelter));
        assertNotNull(record(SHELTER_FILE, AppConstants.SHELTER_ID, shelter.getId()));
    }

    /**
     * Tests that a saved shelter keeps how much room it has for pets.
     */
    @Test
    public void savedShelterIncludesPetSpace() throws Exception {
        Shelter shelter = shelter(UUID.randomUUID());
        DataWriter.saveShelter(shelter);
        JSONObject saved = record(SHELTER_FILE, AppConstants.SHELTER_ID, shelter.getId());
        assertNotNull(saved);
        assertEquals(8, ((Number) saved.get(AppConstants.SHELTER_PET_CAPACITY)).intValue());
        assertEquals(3, ((Number) saved.get(AppConstants.SHELTER_CURRENT_PET_OCCUPANCY)).intValue());
    }

    /**
     * Tests that a shelter with no manager is saved with an empty admin.
     */
    @Test
    public void shelterWithoutAdminIsSavedWithEmptyAdmin() throws Exception {
        Shelter shelter = shelter(null);
        DataWriter.saveShelter(shelter);
        JSONObject saved = record(SHELTER_FILE, AppConstants.SHELTER_ID, shelter.getId());
        assertNotNull(saved);
        assertEquals("", saved.get(AppConstants.SHELTER_ADMIN));
    }

    /**
     * Tests that saving a missing list of users fails.
     */
    @Test
    public void savingNullUserListReturnsFalse() {
        assertFalse(DataWriter.saveUsers(null));
    }

    /**
     * Tests that saving all users puts the coordinator in the coordinator file.
     */
    @Test
    public void savingUsersPutsCoordinatorInCoordinatorFile() throws Exception {
        Coordinator coordinator = coordinator();
        saveAllUsers(coordinator, requester(3), volunteer());
        assertNotNull(record(COORDINATOR_FILE, AppConstants.USER_ID, coordinator.getUserId()));
    }

    /**
     * Tests that saving all users puts the aid requester in the requester file.
     */
    @Test
    public void savingUsersPutsRequesterInRequesterFile() throws Exception {
        saveAllUsers(coordinator(), requester(3), volunteer());
        assertNotNull(record(REQUESTER_FILE, AppConstants.USER_ID, requesterID));
    }

    /**
     * Tests that saving all users puts the volunteer in the volunteer file.
     */
    @Test
    public void savingUsersPutsVolunteerInVolunteerFile() throws Exception {
        Volunteer volunteer = volunteer();
        saveAllUsers(coordinator(), requester(3), volunteer);
        assertNotNull(record(VOLUNTEER_FILE, AppConstants.USER_ID, volunteer.getUserId()));
    }

    /**
     * Tests that saving a missing list of requests fails.
     */
    @Test
    public void savingNullRequestListReturnsFalse() {
        assertFalse(DataWriter.saveRequests(null));
    }

    /**
     * Tests that saving a list of requests writes every request in it.
     */
    @Test
    public void savingRequestsWritesEveryRequest() throws Exception {
        ArrayList<HelpRequest> requests = new ArrayList<HelpRequest>();
        requests.add(personalRequest(requester(3)));
        requests.add(infrastructureRequest());
        assertTrue(DataWriter.saveRequests(requests));
        assertEquals(2, records(dataFile(REQUEST_FILE)).size());
    }

    /**
     * Tests that removing a record deletes it from the file.
     */
    @Test
    public void removingRecordDeletesIt() throws Exception {
        File file = twoRecordFile();
        assertTrue(DataWriter.removeRecord(file.getPath(), "id", "a"));
        for (Object record : records(file)) {
            assertNotEquals("a", ((JSONObject) record).get("id"));
        }
    }

    /**
     * Tests that removing a record keeps the other records.
     */
    @Test
    public void removingRecordKeepsOtherRecords() throws Exception {
        File file = twoRecordFile();
        DataWriter.removeRecord(file.getPath(), "id", "a");
        JSONArray records = records(file);
        assertEquals(1, records.size());
        assertEquals("b", ((JSONObject) records.get(0)).get("id"));
    }

    /**
     * Tests that removing a record that isn't there changes nothing.
     */
    @Test
    public void removingMissingRecordChangesNothing() throws Exception {
        File file = twoRecordFile();
        assertTrue(DataWriter.removeRecord(file.getPath(), "id", "z"));
        assertEquals(2, records(file).size());
    }

    /**
     * Tests that removing from a file that doesn't exist fails.
     */
    @Test
    public void removingFromMissingFileReturnsFalse() {
        File file = new File(folder.getRoot(), "missing.json");
        assertFalse(DataWriter.removeRecord(file.getPath(), "id", "a"));
    }
}
