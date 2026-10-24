package com.model;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author Nicolas Gauvin
 * Class responsible for writing system data out to JSON files.
 */
public class DataWriter extends AppConstants {

    /**
     * Saves one aid requester to the requester JSON file.
     * @param requester Aid requester to save
     * @return true if successful, false otherwise
     */
    public static boolean saveRequester(AidRequester requester) {
        return saveRecord(REQUESTER_JSON, getRequesterJSON(requester), USER_ID);
    }

    /**
     * Saves one help request to the help request JSON file.
     * @param request Help request to save
     * @return true if successful, false otherwise
     */
    public static boolean saveRequest(HelpRequest request) {
        return saveRecord(REQUEST_JSON, getRequestJSON(request), REQUEST_ID);
    }

    /**
     * Adds a record to a JSON file, or replaces it if one with the same ID already exists.
     * @param filename JSON file to save to
     * @param record Record to save
     * @param idKey Key used to find the ID of the record
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("unchecked")
    private static boolean saveRecord(String filename, JSONObject record, String idKey) {
        JSONArray records;

        try (FileReader reader = new FileReader(filename)) {
            records = (JSONArray) new JSONParser().parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < records.size(); i++) {
            JSONObject savedRecord = (JSONObject) records.get(i);
            if (record.get(idKey).equals(savedRecord.get(idKey))) {
                records.set(i, record);
                return writeToFile(filename, records);
            }
        }

        records.add(record);
        return writeToFile(filename, records);
    }

    /**
     * Removes the record with the given ID from a JSON file. Nothing happens if it isn't there.
     * @param filename JSON file to remove from
     * @param idKey Key used to find the ID of the record
     * @param id ID of the record to remove
     * @return true if successful, false otherwise
     */
    public static boolean removeRecord(String filename, String idKey, String id) {
        JSONArray records;

        try (FileReader reader = new FileReader(filename)) {
            records = (JSONArray) new JSONParser().parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < records.size(); i++) {
            JSONObject savedRecord = (JSONObject) records.get(i);
            if (id.equals(savedRecord.get(idKey))) {
                records.remove(i);
                return writeToFile(filename, records);
            }
        }
        return true;
    }

    /**
     * Saves all users (AidCoordinators, AidRequesters, Volunteers) to their respective JSON files.
     * @param users List of users to write
     * @return true if successful, false otherwise
     */
    public static boolean saveUsers(ArrayList<User> users) {
        if (users == null) return false;

        JSONArray coordinatorList = new JSONArray();
        JSONArray requesterList = new JSONArray();
        JSONArray volunteerList = new JSONArray();

        for (User user : users) {
            if (user instanceof Coordinator) {
                coordinatorList.add(getCoordinatorJSON((Coordinator) user));
            } else if (user instanceof AidRequester) {
                requesterList.add(getRequesterJSON((AidRequester) user));
            } else if (user instanceof Volunteer) {
                volunteerList.add(getVolunteerJSON((Volunteer) user));
            }
        }

        boolean s1 = writeToFile(COORDINATOR_JSON, coordinatorList);
        boolean s2 = writeToFile(REQUESTER_JSON, requesterList);
        boolean s3 = writeToFile(VOLUNTEER_JSON, volunteerList);

        return s1 && s2 && s3;
    }

    /**
     * Saves help requests to the help request JSON file.
     * @param requests List of HelpRequest objects
     * @return true if successful, false otherwise
     */
    public static boolean saveRequests(ArrayList<HelpRequest> requests) {
        if (requests == null) return false;

        JSONArray requestList = new JSONArray();

        for (HelpRequest request : requests) {
            requestList.add(getRequestJSON(request));
        }

        return writeToFile(REQUEST_JSON, requestList);
    }

    // --- Helper Methods ---

    /**
     * Converts the fields every user has into a JSON object.
     * @param user User to convert
     * @return JSON object of the user
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getUserBaseJSON(User user) {
        JSONObject obj = new JSONObject();
        obj.put(USER_ID, user.getUserId() != null ? user.getUserId().toString() : "");
        obj.put(USER_FIRST_NAME, user.getFirstName());
        obj.put(USER_LAST_NAME, user.getLastName());
        obj.put(USER_EMAIL, user.getEmail());
        obj.put(USER_PHONE, user.getPhone());
        obj.put(REQUEST_LOCATION, getLocationJSON(user.getLocation()));
        obj.put(USER_BIRTHDATE, user.getBirthDate() != null ? user.getBirthDate().toString() : "");
        obj.put(USER_USERNAME, user.getUserName());
        obj.put(USER_PASSWORD, user.getPassword());
        obj.put(USER_NOTIFICATION_PREFERENCE, user.getNotificationPreference() != null ? user.getNotificationPreference().name() : "");

        JSONArray contacts = new JSONArray();
        if (user.getEmergencyContacts() != null) {
            contacts.addAll(user.getEmergencyContacts());
        }
        obj.put(USER_EMERGENCY_CONTACTS, contacts);

        return obj;
    }

    /**
     * Converts a coordinator into a JSON object.
     * @param coordinator Coordinator to convert
     * @return JSON object of the coordinator
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getCoordinatorJSON(Coordinator coordinator) {
        JSONObject obj = getUserBaseJSON(coordinator);
        obj.put(COORDINATOR_ROLE, coordinator.getRole() != null ? coordinator.getRole().name() : "");
        return obj;
    }

    /**
     * Converts an aid requester into a JSON object.
     * @param requester Aid requester to convert
     * @return JSON object of the aid requester
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getRequesterJSON(AidRequester requester) {
        JSONObject obj = getUserBaseJSON(requester);
        obj.put(USER_HOUSEHOLDSIZE, requester.getHouseholdSize());
        obj.put(USER_ASSISTANCE_TYPE, requester.getAssistanceType() != null ? requester.getAssistanceType().name() : "");

        JSONArray pastReqs = new JSONArray();
        if (requester.getPastRequests() != null) {
            pastReqs.addAll(requester.getPastRequests());
        }
        obj.put(USER_PAST_REQUESTS, pastReqs);

        return obj;
    }

    /**
     * Converts a volunteer into a JSON object.
     * @param volunteer Volunteer to convert
     * @return JSON object of the volunteer
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getVolunteerJSON(Volunteer volunteer) {
        JSONObject obj = getUserBaseJSON(volunteer);
        obj.put(USER_AVAILIBILITY, volunteer.isAvailability());
        obj.put(USER_TRAVEL_DISTANCE, volunteer.getTravelDistance());

        JSONArray skills = new JSONArray();
        if (volunteer.getSkills() != null) {
            skills.addAll(volunteer.getSkills());
        }
        obj.put(USER_SKILLS, skills);

        JSONArray credentials = new JSONArray();
        if (volunteer.getCredentials() != null) {
            for (Credential cred : volunteer.getCredentials()) {
                JSONObject credObj = new JSONObject();
                credObj.put(USER_CREDENTIALS_TYPE, cred.getType() != null ? cred.getType().name() : "");
                credObj.put(USER_CREDENTIALS_STATUS, cred.getStatus());
                credentials.add(credObj);
            }
        }
        obj.put(USER_CREDENTIALS, credentials);

        JSONArray supplies = new JSONArray();
        if (volunteer.getSupplies() != null) {
            supplies.addAll(volunteer.getSupplies());
        }
        obj.put(USER_SUPPLIES, supplies);

        obj.put(USER_VERIFIED, volunteer.isVerified());
        obj.put(USER_DISPACHED, volunteer.isDispatched());
        obj.put(USER_VOLUNTEER_HOURS, volunteer.getVolunteerHours());

        return obj;
    }

    /**
     * Converts a help request into a JSON object.
     * @param request Help request to convert
     * @return JSON object of the help request
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getRequestJSON(HelpRequest request) {
        JSONObject obj = new JSONObject();
        obj.put(REQUEST_ID, request.getRequestID() != null ? request.getRequestID().toString() : "");
        obj.put(REQUEST_TYPE, request.getRequestType() != null ? request.getRequestType().name() : "");
        obj.put(REQUEST_USER_ID, request.getUser() != null && request.getUser().getUserId() != null 
                ? request.getUser().getUserId().toString() : "");
        obj.put(REQUEST_COMMENT, request.getComment());
        obj.put(REQUEST_ASSISTANCE_TYPE, request.getAssistanceType() != null ? request.getAssistanceType().name() : "");
        obj.put(REQUEST_URGENCY, request.getUrgency() != null ? request.getUrgency().name() : "");
        obj.put(REQUEST_STATUS, request.getRequestStatus() != null ? request.getRequestStatus().toString() : "");
        obj.put(REQUEST_LOCATION, getLocationJSON(request.getLocation()));
        obj.put(REQUETS_PHOTO, request.getPhoto());
        obj.put(REQUEST_ASSIGNED_VOLUNTEER, request.getAssignedVolunteerID() != null
                ? request.getAssignedVolunteerID().toString() : "");
        obj.put(REQUEST_SELECTED_SHELTER, request.getSelectedShelterID() != null
                ? request.getSelectedShelterID().toString() : "");

        if (request instanceof PersonalRequest) {
            PersonalRequest pr = (PersonalRequest) request;
            obj.put(REQUEST_HOUSEHOLD_SIZE, pr.getHouseholdSize());
            obj.put(REQUEST_ACCESS_INTRUCTIONS, pr.getAccessInstructions());
            obj.put(REQUEST_PETS, pr.getPets());
            obj.put(REQUEST_CHILDREN, pr.getChildren());

            JSONArray members = new JSONArray();
            members.addAll(pr.getHouseholdMembers());
            obj.put(REQUEST_HOUSEHOLD_MEMBERS, members);

            JSONArray petNames = new JSONArray();
            petNames.addAll(pr.getPetNames());
            obj.put(REQUEST_PET_NAMES, petNames);

            obj.put(REQUEST_MEDICAL_NOTE, pr.getMedicalNote());
        } else if (request instanceof InfrustructureRequest) {
            InfrustructureRequest ir = (InfrustructureRequest) request;
            obj.put(REQUEST_HAZARD_TYPE, ir.getHazardType() != null ? ir.getHazardType().name() : "");
            obj.put(REQUEST_REPORT_TIME, ir.getReportTime() != null ? ir.getReportTime().toString() : "");
        }

        return obj;
    }

    /**
     * Converts a location into a JSON object.
     * @param loc Location to convert
     * @return JSON object of the location, or null if the location is null
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getLocationJSON(Location loc) {
        if (loc == null) return null;
        JSONObject obj = new JSONObject();
        obj.put(LOCATION_LATITUDE, loc.getLatitude());
        obj.put(LOCATION_LONGITUDE, loc.getLongitude());
        obj.put(LOCATION_STREET_ADDRESS, loc.getStreetAddress());
        obj.put(LOCATION_APPARTMENT_NUMBER, loc.getApartmentNum());
        obj.put(LOCATION_CITY, loc.getCity());
        obj.put(LOCATION_STATE, loc.getState());
        obj.put(LOCATION_ZIP, loc.getZipcode());
        return obj;
    }

    /**
     * Writes a JSON array to a file.
     * @param filename File to write to
     * @param jsonArray JSON array to write
     * @return true if successful, false otherwise
     */
    private static boolean writeToFile(String filename, JSONArray jsonArray) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(jsonArray.toJSONString());
            file.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves one volunteer to the volunteer JSON file.
     * @param volunteer Volunteer to save
     * @return true if successful, false otherwise
     */
    public static boolean saveVolunteer(Volunteer volunteer) {
        return saveRecord(VOLUNTEER_JSON, getVolunteerJSON(volunteer), USER_ID);
    }

    /**
     * Saves one coordinator to the coordinator JSON file.
     * @param coordinator Coordinator to save
     * @return true if successful, false otherwise
     */
    public static boolean saveCoordinator(Coordinator coordinator) {
        return saveRecord(COORDINATOR_JSON, getCoordinatorJSON(coordinator), USER_ID);
    }

    /**
     * Saves one shelter to the shelter JSON file.
     * @param shelter Shelter to save
     * @return true if successful, false otherwise
     */
    public static boolean saveShelter(Shelter shelter) {
        return saveRecord(SHELTER_JSON, getShelterJSON(shelter), SHELTER_ID);
    }

    /**
     * Converts a shelter into a JSON object.
     * @param shelter Shelter to convert
     * @return JSON object of the shelter
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getShelterJSON(Shelter shelter) {
        JSONObject obj = new JSONObject();
        obj.put(SHELTER_NAME, shelter.getName());
        obj.put(SHELTER_ID, shelter.getId().toString());
        obj.put(SHELTER_LOCATION, getLocationJSON(shelter.getLocation()));
        obj.put(SHELTER_CURRENT_OCCUPANCY, shelter.getCurrentOccupancy());
        obj.put(SHELTER_CAPACITY, shelter.getCapacity());
        obj.put(SHELTER_PET_FRIENDLY, shelter.isPetFriendly());
        obj.put(SHELTER_MEDICAL_STAFF, shelter.hasMedicalStaff());

        JSONArray accessibility = new JSONArray();
        if (shelter.getAccessibilityInfo() != null) {
            accessibility.addAll(shelter.getAccessibilityInfo());
        }
        obj.put(SHELTER_ACCESSIBILITY_INFO, accessibility);

        obj.put(SHELTER_HAS_POWER, shelter.hasPower());
        obj.put(SHELTER_STATUS, shelter.getStatus() != null ? shelter.getStatus().name() : "");
        obj.put(SHELTER_PET_CAPACITY, shelter.getPetCapacity());
        obj.put(SHELTER_CURRENT_PET_OCCUPANCY, shelter.getCurrentPetOccupancy());
        obj.put(SHELTER_ADMIN, shelter.getAdmin() != null ? shelter.getAdmin().toString() : "");

        JSONArray occupants = new JSONArray();
        occupants.addAll(shelter.getOccupants());
        obj.put(SHELTER_OCCUPANTS, occupants);

        JSONArray pets = new JSONArray();
        pets.addAll(shelter.getPets());
        obj.put(SHELTER_PETS, pets);

        return obj;
    }
}