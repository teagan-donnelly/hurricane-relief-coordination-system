package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Driver that runs through the Carter family hurricane relief scenario.
 * @author Teagan Donnelly
 */
public class Driver {

    private static final int WIDTH = 64;
    private static final String LINE = "=".repeat(WIDTH);

    /**
     * Runs the scenario.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println("  HURRICANE RELIEF SYSTEM: CARTER FAMILY SCENARIO");
        System.out.println(LINE);
        showSavedRecords("Saved data before this run");
        runScenario();
        showSavedRecords("Saved data after this run");
        System.out.println("\n" + LINE);
    }

    /**
     * Prints a heading for a section of the scenario.
     * @param title Heading text
     */
    public static void printSection(String title) {
        String heading = "--- " + title + " ";
        System.out.println("\n" + heading + "-".repeat(Math.max(3, WIDTH - heading.length())));
    }

    /**
     * Prints a label and value so the values line up.
     * @param label Label to print
     * @param value Value to print
     */
    public static void printField(String label, Object value) {
        System.out.printf("%-20s %s%n", label + ":", value);
    }

    /**
     * Prints a label followed by each item on its own line.
     * @param label Label to print
     * @param items Items to print
     */
    public static void printList(String label, ArrayList<String> items) {
        if (items.isEmpty()) {
            printField(label, "none");
            return;
        }
        System.out.println(label + ":");
        for (String item : items) {
            System.out.println("  - " + item);
        }
    }

    /**
     * Prints long text wrapped so it fits on the screen.
     * @param text Text to print
     */
    public static void printWrapped(String text) {
        String line = "";
        for (String word : text.split(" ")) {
            if (!line.isEmpty() && line.length() + word.length() + 1 > WIDTH - 2) {
                System.out.println("  " + line);
                line = "";
            }
            line += (line.isEmpty() ? "" : " ") + word;
        }
        if (!line.isEmpty()) {
            System.out.println("  " + line);
        }
    }

    /**
     * Prints the Carter request and its shelter exactly as they are saved in the JSON files,
     * so a restart can be checked against what was saved.
     * @param label when this snapshot was taken
     */
    public static void showSavedRecords(String label) {
        UUID requestID = UUID.fromString("b5e1d824-7f93-4046-8d28-249ca2534202");
        printSection(label);

        PersonalRequest saved = null;
        for (HelpRequest current : DataLoader.getRequests()) {
            if (current.getRequestID().equals(requestID) && current instanceof PersonalRequest) {
                saved = (PersonalRequest) current;
            }
        }
        if (saved == null) {
            System.out.println("The Carter request has not been saved yet.");
            return;
        }

        String volunteerName = "none";
        for (Volunteer volunteer : DataLoader.getVolunteers()) {
            if (volunteer.getUserId().equals(saved.getAssignedVolunteerID())) {
                volunteerName = volunteer.getFirstName() + " " + volunteer.getLastName();
            }
        }
        printField("Request status", saved.getRequestStatus());
        printField("Assigned volunteer", volunteerName);
        printList("Household", saved.getHouseholdMembers());
        printList("Pets", saved.getPetNames());

        Shelter shelter = new ShelterManager().findShelter(saved.getSelectedShelterID());
        if (shelter == null) {
            printField("Selected shelter", "none");
        } else {
            printField("Selected shelter", shelter.getName());
            printField("Shelter occupancy", shelter.getCurrentOccupancy() + "/" +
                shelter.getCapacity() + " people, " + shelter.getCurrentPetOccupancy() + "/" +
                shelter.getPetCapacity() + " pets");
            printList("Shelter occupants", shelter.getOccupants());
            printList("Shelter pets", shelter.getPets());
        }
    }

    public static void runScenario() {
        Location home = new Location(32.7765, -79.9311,
            "123 Palmetto Lane", 0, "Charleston", "SC", "29401");

        ArrayList<String> emergencyContacts = new ArrayList<>();
        emergencyContacts.add("Emily Carter: 843-555-0148");
        ArrayList<String> pastRequests = new ArrayList<>();

        UUID fatherID = UUID.fromString("a4f0c713-6e82-4f35-9c17-138b91423101");
        UUID requestID = UUID.fromString("b5e1d824-7f93-4046-8d28-249ca2534202");

        AidRequester father = new AidRequester("Michael", "Carter", fatherID,
            "michael.carter@example.com", "843-555-0147", home,
            LocalDate.of(1987, 4, 12), "michaelcarter", "CarterDemo123",
            NotificationType.EMAIL, emergencyContacts, 3,
            AssistanceType.TRANSPORT, pastRequests);

        printSection("Michael Carter submits a relief request");
        if (!DataWriter.saveRequester(father)) {
            System.out.println("Could not save Michael Carter's account.");
            return;
        }

        String comment = "Hurricane Scarlett has caused a power outage and rising floodwater. " +
            "Michael Carter, age 39, and Emily Carter, age 37, need transportation " +
            "for themselves and their daughter Sophie, age 8: three people total. " +
            "Sophie's asthma symptoms have worsened in the hot, humid house. " +
            "She needs medical assistance and a shelter with medical staff. " +
            "The family is bringing one dog, Buddy, and will not leave him behind. " +
            "Transportation and the shelter must accommodate Buddy. " +
            "Floodwater prevents the family from safely using their car. " +
            "Contact Michael at 843-555-0147 or michael.carter@example.com. " +
            "Emily can also be reached at 843-555-0148.";

        PersonalRequest request = new PersonalRequest(requestID, RequestType.PERSONAL,
            father, comment, AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.PENDING_REVIEW, home, "", 3,
            "Rising floodwater surrounds the home; the family cannot safely use their car.",
            1, 1);

        for (HelpRequest saved : DataLoader.getRequests()) {
            if (saved.getRequestID().equals(requestID) && saved instanceof PersonalRequest) {
                request = (PersonalRequest) saved;
            }
        }

        addFamilyDetails(request);

        if (DataWriter.saveRequest(request)) {
            System.out.println("Michael Carter's account and relief request were saved.");
            printField("Location", "123 Palmetto Lane, Charleston, SC 29401");
            printField("Assistance needed", request.getAssistanceType());
            printField("Urgency", request.getUrgency());
            System.out.println("Comment:");
            printWrapped(comment);
        } else {
            System.out.println("Account saved, but the relief request could not be saved.");
        }

        printSection("Rosa Delgado logs in as a volunteer");
        Volunteer rosa = registerRosa();
        if (rosa == null) {
            return;
        }

        ArrayList<User> users = new ArrayList<>();
        users.add(rosa);
        AccountManager accounts = new AccountManager(users);

        if (!accounts.login("rosadelgado", "RosaDemo123")) {
            System.out.println("Rosa could not log in.");
            return;
        }

        System.out.println("Rosa Delgado logged in.");
        boolean alreadyHandled = request.getAssignedVolunteerID() != null ||
            request.getRequestStatus() == RequestStatus.COMPLETED ||
            request.getRequestStatus() == RequestStatus.CANCELED;

        if (!alreadyHandled) {
            ArrayList<HelpRequest> requests = createScenarioRequests(request);
            if (requests == null) {
                return;
            }
            showRecommendedRequests(rosa, requests);
        }
        showRequestDetails(request);
        findShelterAndAssign(rosa, request);

        exportTrip(rosa, request);
        showRolePermissions(rosa, request);
        checkInAtShelter(rosa, request);
    }

    /**
     * Adds the Carter family's household members, pet, and medical note to the request.
     * @param request Carter family's help request
     */
    public static void addFamilyDetails(PersonalRequest request) {
        if (!request.getHouseholdMembers().isEmpty()) {
            return;
        }
        request.addHouseholdMember("Michael Carter, age 39");
        request.addHouseholdMember("Emily Carter, age 37");
        request.addHouseholdMember("Sophie Carter, age 8");
        request.addPet("Buddy (dog)");
        request.setMedicalNote("Sophie Carter (age 8) has worsening asthma symptoms and needs " +
            "medical assistance. Take the family to a shelter with medical staff.");
    }

    /**
     * Loads Rosa Delgado's volunteer account, or creates and saves it if it doesn't exist.
     * @return Rosa's volunteer account, or null if it could not be saved
     */
    public static Volunteer registerRosa() {
        UUID rosaID = UUID.fromString("c6f2e935-8a04-4157-9e39-35adb3645303");
        for (Volunteer saved : DataLoader.getVolunteers()) {
            if (saved.getUserId().equals(rosaID)) {
                System.out.println("Loaded Rosa Delgado's saved volunteer account.");
                return saved;
            }
        }
        Location rosaHome = new Location(32.7843, -79.9389,
            "58 Rutledge Avenue", 0, "Charleston", "SC", "29401");

        ArrayList<String> emergencyContacts = new ArrayList<>();
        emergencyContacts.add("Luis Delgado: 843-555-0172");

        ArrayList<String> skills = new ArrayList<>();
        skills.add("Transportation");

        ArrayList<String> supplies = new ArrayList<>();
        supplies.add("Ford F-350 truck");

        Volunteer rosa = new Volunteer("Rosa", "Delgado",
            UUID.fromString("c6f2e935-8a04-4157-9e39-35adb3645303"),
            "rosa.delgado@example.com", "843-555-0171", rosaHome,
            LocalDate.of(1984, 6, 3), "rosadelgado", "RosaDemo123",
            NotificationType.SMS, emergencyContacts,
            true, 25.0, skills, new ArrayList<Credential>(), supplies,
            true, false, 0.0);

        if (DataWriter.saveVolunteer(rosa)) {
            System.out.println("Rosa Delgado was registered as a volunteer.");
            printField("Specialty", "Transportation (Ford F-350 truck)");
            return rosa;
        } else {
            System.out.println("Could not save Rosa Delgado's volunteer account.");
            return null;
        }
    }

    /**
     * Creates a list of help requests for the scenario, including the Carter request.
     * @param carterRequest Carter family's help request
     * @return List of help requests
     */
    public static ArrayList<HelpRequest> createScenarioRequests(HelpRequest carterRequest) {
        ArrayList<HelpRequest> requests = new ArrayList<>();
        requests.add(carterRequest);

        String[] firstNames = {"James", "Olivia", "Henry", "Ava", "Noah", "Mia"};
        String[] lastNames = {"Wilson", "Brown", "Davis", "Green", "Hall", "King"};
        String[] streets = {"100 Meeting Street", "200 King Street", "300 East Bay Street",
                            "400 Savannah Highway", "500 Folly Road", "600 Sam Rittenberg Boulevard"};
        double[] latitudes = {32.7770, 32.7820, 32.7890, 32.7950, 32.7500, 32.8200};
        double[] longitudes = {-79.9310, -79.9340, -79.9250, -79.9800, -79.9800, -80.0200};
        AssistanceType[] types = {AssistanceType.FOOD_SUPPLY, AssistanceType.SHELTER,
            AssistanceType.MEDICAL_SUPPLIES, AssistanceType.TRANSPORT,
            AssistanceType.TRANSPORT, AssistanceType.TRANSPORT};
        String[] comments = {"Need food and bottled water.", "Need temporary shelter.",
            "Need medical supplies delivered.", "Need transportation to a shelter.",
            "Cannot use my car and need transportation.", "Need a ride to a shelter."};

        for (int i = 0; i < firstNames.length; i++) {
            Location location = new Location(latitudes[i], longitudes[i], streets[i],
                0, "Charleston", "SC", "29401");
            UUID userID = UUID.fromString("d7000000-0000-4000-8000-00000000000" + i);
            UUID requestID = UUID.fromString("e8000000-0000-4000-8000-00000000000" + i);

            AidRequester requester = new AidRequester(firstNames[i], lastNames[i], userID,
                firstNames[i].toLowerCase() + "@example.com", "843-555-012" + i,
                location, LocalDate.of(1990, 1, 1), firstNames[i].toLowerCase() + "demo",
                "ScenarioDemo123", NotificationType.EMAIL, new ArrayList<String>(),
                1, types[i], new ArrayList<String>());

            PersonalRequest request = new PersonalRequest(requestID, RequestType.PERSONAL,
                requester, comments[i], types[i], Urgency.HIGH_PRIORITY,
                RequestStatus.AWAITING_ASSIGNMENT, location, "", 1, "", 0, 0);

            if (!DataWriter.saveRequester(requester) || !DataWriter.saveRequest(request)) {
                System.out.println("Could not save the other scenario requests.");
                return null;
            }
            requests.add(request);
        }

        return requests;
    }

    /**
     * Checks if a volunteer's skills match what a request needs.
     * @param volunteer Volunteer being checked
     * @param request Help request being checked
     * @return true if the volunteer matches the request, false otherwise
     */
    public static boolean matchesSpecialty(Volunteer volunteer, HelpRequest request) {
        if (request.getAssistanceType() == AssistanceType.TRANSPORT) {
            for (String skill : volunteer.getSkills()) {
                if ("Transportation".equalsIgnoreCase(skill)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the straight line distance between two locations.
     * @param start Starting location
     * @param end Ending location
     * @return Distance in kilometers
     */
    public static double getDistance(Location start, Location end) {
        double latitude = Math.toRadians(end.getLatitude() - start.getLatitude());
        double longitude = Math.toRadians(end.getLongitude() - start.getLongitude());
        double value = Math.sin(latitude / 2) * Math.sin(latitude / 2) +
            Math.cos(Math.toRadians(start.getLatitude())) *
            Math.cos(Math.toRadians(end.getLatitude())) *
            Math.sin(longitude / 2) * Math.sin(longitude / 2);
        value = Math.max(0, Math.min(1, value));
        return 6371 * 2 * Math.atan2(Math.sqrt(value), Math.sqrt(1 - value));
    }

    /**
     * Prints the open requests and recommends the best one for the volunteer.
     * @param rosa Volunteer looking at requests
     * @param requests List of help requests
     */
    public static void showRecommendedRequests(Volunteer rosa, ArrayList<HelpRequest> requests) {
        ArrayList<HelpRequest> openRequests = new ArrayList<>();
        for (HelpRequest request : requests) {
            if (request.getRequestStatus() == RequestStatus.PENDING_REVIEW ||
                request.getRequestStatus() == RequestStatus.APPROVED ||
                request.getRequestStatus() == RequestStatus.AWAITING_ASSIGNMENT) {
                openRequests.add(request);
            }
        }

        for (int i = 0; i < openRequests.size() - 1; i++) {
            for (int j = 0; j < openRequests.size() - 1 - i; j++) {
                HelpRequest first = openRequests.get(j);
                HelpRequest second = openRequests.get(j + 1);
                boolean firstMatches = matchesSpecialty(rosa, first);
                boolean secondMatches = matchesSpecialty(rosa, second);
                double firstDistance = getDistance(rosa.getLocation(), first.getLocation());
                double secondDistance = getDistance(rosa.getLocation(), second.getLocation());

                if ((!firstMatches && secondMatches) ||
                    (firstMatches == secondMatches && firstDistance > secondDistance)) {
                    openRequests.set(j, second);
                    openRequests.set(j + 1, first);
                }
            }
        }

        printSection("Open requests (" + openRequests.size() + ")");
        System.out.println("Sorted by transportation specialty, then straight-line distance.\n");
        System.out.printf("%-3s %-16s %-18s %s%n", "#", "Requester", "Assistance", "Distance");

        boolean recommended = false;
        for (int i = 0; i < openRequests.size(); i++) {
            HelpRequest request = openRequests.get(i);
            double distance = getDistance(rosa.getLocation(), request.getLocation());
            boolean isRecommended = i == 0 && matchesSpecialty(rosa, request);
            System.out.printf("%-3d %-16s %-18s %5.2f km%s%n", i + 1,
                request.getUser().getFirstName() + " " + request.getUser().getLastName(),
                request.getAssistanceType(), distance, isRecommended ? "  <- RECOMMENDED" : "");
            recommended = recommended || isRecommended;
        }
        if (recommended) {
            System.out.println("\nRecommended because it matches Rosa's transportation specialty");
            System.out.println("and is the closest request with that specialty.");
        }
    }

    /**
     * Prints the details of a request.
     * @param request Help request to print
     */
    public static void showRequestDetails(PersonalRequest request) {
        printSection("Rosa opens the Carter family request");
        printField("People", request.getHouseholdSize() + " (" +
            (request.getHouseholdSize() - request.getChildren()) + " adults, " +
            request.getChildren() + (request.getChildren() == 1 ? " child)" : " children)"));
        printField("Pets", request.getPetNames().isEmpty() ? request.getPets()
            : String.join(", ", request.getPetNames()));
        printField("Pickup", request.getLocation().getStreetAddress() + ", " +
            request.getLocation().getCity() + ", " + request.getLocation().getState() + " " +
            request.getLocation().getZipcode());
        printField("Phone", request.getUser().getPhone());
        printField("Email", request.getUser().getEmail());
        printField("Status", request.getRequestStatus());
        System.out.println("Access notes:");
        printWrapped(request.getAccessInstructions());
        System.out.println("Medical note:");
        printWrapped(request.getMedicalNote());
    }

    /**
     * Finds a shelter for the family and assigns the volunteer to the request.
     * @param rosa Volunteer being assigned
     * @param request Help request being assigned
     */
    public static void findShelterAndAssign(Volunteer rosa, PersonalRequest request) {
        ShelterManager shelterManager = new ShelterManager();
        printSection("Rosa finds a shelter and takes the request");
        if (request.getAssignedVolunteerID() != null) {
            Shelter savedShelter = shelterManager.findShelter(request.getSelectedShelterID());
            System.out.println("This request was already assigned in an earlier run.");
            printField("Request status", request.getRequestStatus());
            printField("Assigned volunteer", rosa.getUserId().equals(request.getAssignedVolunteerID())
                ? rosa.getFirstName() + " " + rosa.getLastName() : request.getAssignedVolunteerID());
            if (savedShelter != null) {
                printField("Selected shelter", savedShelter.getName() + ", " +
                    savedShelter.getLocation().getStreetAddress());
            }
            return;
        }
        if (request.getRequestStatus() == RequestStatus.COMPLETED ||
            request.getRequestStatus() == RequestStatus.CANCELED ||
            request.getRequestStatus() == RequestStatus.DENIED || rosa.isDispatched()) {
            System.out.println("This request or volunteer is not available for assignment.");
            return;
        }
        System.out.println("The family needs room for " + request.getHouseholdSize() + " people and " +
            request.getPets() + " dog, plus medical staff.");
        System.out.println("Checking " + shelterManager.getShelters().size() + " shelters:\n");

        for (Shelter shelter : shelterManager.getShelters()) {
            String problem = shelterManager.getProblem(shelter, request, true);
            System.out.printf("  %-42s %s%n", shelter.getName(), problem == null ? "suitable" : problem);
        }

        System.out.println("\nDriving distance to each suitable shelter:");

        Shelter shelter;
        try {
            shelter = shelterManager.findClosestSuitableShelter(request, true);
        } catch (java.io.IOException e) {
            System.out.println("Shelter selection stopped: " + e.getMessage());
            System.out.println("No assignment was made. Check MAP_API_KEY and try again.");
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Shelter selection was interrupted. No assignment was made.");
            return;
        }
        if (shelter == null) {
            System.out.println("No shelter meets the family's needs.");
            return;
        }
        System.out.println();
        printField("Closest shelter", shelter.getName());
        printField("Address", shelter.getLocation().getStreetAddress());
        printField("Open spaces", shelter.getAvailableSpace() + " people, " +
            shelter.getAvailablePetSpace() + " pets (accepts dogs, has medical staff)");
        System.out.println();

        VolunteerManager.getInstance().assignVolunteer(rosa, request);
        if (!rosa.getUserId().equals(request.getAssignedVolunteerID())) {
            System.out.println("Assignment was not made.");
            return;
        }
        request.setSelectedShelterID(shelter.getId());

        if (!DataWriter.saveRequest(request) || !DataWriter.saveVolunteer(rosa)) {
            System.out.println("Could not save the assignment.");
            return;
        }
        printField("Request status", request.getRequestStatus());

        for (HelpRequest saved : DataLoader.getRequests()) {
            if (saved.getRequestID().equals(request.getRequestID())) {
                for (Volunteer volunteer : DataLoader.getVolunteers()) {
                    if (volunteer.getUserId().equals(saved.getAssignedVolunteerID())) {
                        printField("Others see", saved.getRequestStatus() +
                            ", handled by " + volunteer.getFirstName() + " " + volunteer.getLastName());
                    }
                }
            }
        }
    }

    /**
     * Exports the volunteer's trip info to a file.
     * @param rosa Volunteer exporting the trip
     * @param request Help request for the trip
     */
    public static void exportTrip(Volunteer rosa, PersonalRequest request) {
        ShelterManager shelterManager = new ShelterManager();
        Shelter shelter = shelterManager.findShelter(request.getSelectedShelterID());
        printSection("Rosa exports her trip sheet");
        if (shelter == null) {
            System.out.println("No shelter has been selected, so the trip cannot be exported.");
            return;
        }
        rosa.exportTripInfo(request, shelter, "carter-trip.txt");
    }

    /**
     * Shows that a volunteer cannot mark a request complete.
     * @param rosa Volunteer trying to complete the request
     * @param request Help request being completed
     */
    public static void showRolePermissions(Volunteer rosa, PersonalRequest request) {
        printSection("Rosa tries to close the request");
        System.out.println("Rosa drops the family off and tries to mark the request complete:");
        request.markComplete(rosa);
        System.out.println("\nRosa tries to set the status to COMPLETED directly:");
        request.setRequestStatus(RequestStatus.COMPLETED);
        System.out.println();
        printField("Request status", request.getRequestStatus() + " (unchanged)");
    }

    /**
     * Loads Felix's coordinator account, or creates and saves it if it doesn't exist.
     * @return Felix's coordinator account, or null if it could not be saved
     */
    public static Coordinator registerFelix() {
        UUID felixID = UUID.fromString("f9a3b046-1c25-4a68-8f4a-46bec4756404");
        for (Coordinator saved : DataLoader.getCoordinators()) {
            if (saved.getUserId().equals(felixID)) {
                return saved;
            }
        }

        Location felixLocation = new Location(32.792, -79.9512,
            "244 President Street", 0, "Charleston", "SC", "29403");
        ArrayList<String> emergencyContacts = new ArrayList<>();
        emergencyContacts.add("Ana Moreno: 843-555-0190");

        Coordinator felix = new Coordinator(CoordinatorRole.SHELTER, "Felix", "Moreno",
            felixID, "felix.moreno@example.com", "843-555-0189", "felixmoreno", "FelixDemo123",
            felixLocation, LocalDate.of(1979, 9, 15), NotificationType.APP_PUSH,
            emergencyContacts, VolunteerManager.getInstance());

        if (!DataWriter.saveCoordinator(felix)) {
            System.out.println("Could not save Felix's account.");
            return null;
        }
        return felix;
    }

    /**
     * Prints the details of a shelter.
     * @param shelter Shelter to print
     */
    public static void showShelter(Shelter shelter) {
        Location location = shelter.getLocation();
        printField("Shelter", shelter.getName());
        printField("Location", location.getStreetAddress() + ", " + location.getCity() +
            ", " + location.getState() + " " + location.getZipcode());
        printField("Occupancy", shelter.getCurrentOccupancy() + "/" + shelter.getCapacity() +
            " (" + shelter.getAvailableSpace() + " spaces open)");
        printList("Occupants", shelter.getOccupants());
        printList("Pets", shelter.getPets());
    }

    /**
     * Has Felix check the family into the shelter and mark the request complete.
     * @param rosa Volunteer who brought the family
     * @param request Help request being completed
     */
    public static void checkInAtShelter(Volunteer rosa, PersonalRequest request) {
        Coordinator felix = registerFelix();
        if (felix == null) {
            return;
        }

        ArrayList<User> users = new ArrayList<>();
        users.add(felix);
        AccountManager accounts = new AccountManager(users);
        if (!accounts.login("felixmoreno", "FelixDemo123")) {
            System.out.println("Felix could not log in.");
            return;
        }
        printSection("Felix Moreno checks the family in");
        System.out.println("Felix " + felix.getLastName() + " (shelter manager) logged in.\n");

        ShelterManager shelterManager = new ShelterManager();
        Shelter shelter = null;
        for (Shelter current : shelterManager.getShelters()) {
            if (current.getAdmin() != null && current.getAdmin().equals(felix.getUserId())) {
                shelter = current;
            }
        }
        if (shelter == null) {
            shelter = shelterManager.findShelter(request.getSelectedShelterID());
            if (shelter == null) {
                System.out.println("Felix does not manage a shelter yet.");
                return;
            }
            shelter.setAdmin(felix.getUserId());
        }
        showShelter(shelter);

        if (request.getRequestStatus() == RequestStatus.COMPLETED) {
            System.out.println("\nThe Carter family is already checked in and the request is complete.");
            return;
        }
        if (!shelter.getId().equals(request.getSelectedShelterID())) {
            System.out.println("\nThe Carter family is not headed to Felix's shelter.");
            return;
        }

        System.out.println("\nFelix checks the Carter family and Buddy into the shelter.\n");
        int occupancyBefore = shelter.getCurrentOccupancy();
        if (!shelter.checkIn(request)) {
            return;
        }
        showShelter(shelter);
        printField("Occupancy change", occupancyBefore + " -> " + shelter.getCurrentOccupancy() +
            " (+" + (shelter.getCurrentOccupancy() - occupancyBefore) + ")");

        System.out.println("\nFelix accidentally tries to check the family in again:");
        shelter.checkIn(request);
        printField("Occupancy", shelter.getCurrentOccupancy() + " (unchanged), " +
            shelter.getOccupants().size() + " occupants and " + shelter.getPets().size() +
            (shelter.getPets().size() == 1 ? " pet" : " pets") + " on record");

        System.out.println("\nFelix marks the relief request complete.");
        if (!request.markComplete(felix)) {
            return;
        }
        rosa.completeAssignment();

        if (!DataWriter.saveShelter(shelter) || !DataWriter.saveRequest(request) ||
            !DataWriter.saveVolunteer(rosa)) {
            System.out.println("Could not save the check-in.");
            return;
        }
        printField("Request status", request.getRequestStatus());
        printField("Rosa dispatched", rosa.isDispatched() + " (free for new requests)");
    }
}