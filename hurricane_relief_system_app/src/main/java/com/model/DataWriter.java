package com.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Nicolas Gauvin
 * Class responsible for writing system data out to JSON files.
 */
public class DataWriter extends AppConstants {

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
            if (user instanceof AidCoordinator) {
                coordinatorList.add(getCoordinatorJSON((AidCoordinator) user));
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

    @SuppressWarnings("unchecked")
    private static JSONObject getUserBaseJSON(User user) {
        JSONObject obj = new JSONObject();
        obj.put(USER_ID, user.getUserId() != null ? user.getUserId().toString() : "");
        obj.put(USER_FIRST_NAME, user.getFirstName());
        obj.put(USER_LAST_NAME, user.getLastName());
        obj.put(USER_EMAIL, user.getEmail());
        obj.put(REQUEST_LOCATION, getLocationJSON(user.getLocation()));
        obj.put(USER_BIRTHDATE, user.getBirthDate() != null ? user.getBirthDate().toString() : "");
        obj.put(USER_USERNAME, user.getUserName());
        obj.put(USER_PASSWORD, user.getPassword());
        obj.put(USER_NOIFICATION_PREFEREMCE, user.getNotificationPreference() != null ? user.getNotificationPreference().name() : "");

        JSONArray contacts = new JSONArray();
        if (user.getEmergencyContacts() != null) {
            contacts.addAll(user.getEmergencyContacts());
        }
        obj.put(USER_EMERGENCY_CONTACTS, contacts);

        return obj;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject getCoordinatorJSON(AidCoordinator coordinator) {
        return getUserBaseJSON(coordinator);
    }

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

        if (request instanceof PersonalRequest) {
            PersonalRequest pr = (PersonalRequest) request;
            obj.put(REQUEST_HOUSEHOLD_SIZE, pr.getHouseholdSize());
            obj.put(REQUEST_ACCESS_INTRUCTIONS, pr.getAccessInstructions());
        } else if (request instanceof InfrustructureRequest) {
            InfrustructureRequest ir = (InfrustructureRequest) request;
            obj.put(REQUEST_HAZARD_TYPE, ir.getHazardType() != null ? ir.getHazardType().name() : "");
            obj.put(REQUEST_REPORT_TIME, ir.getReportTime() != null ? ir.getReportTime().toString() : "");
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject getLocationJSON(Location loc) {
        if (loc == null) return null;
        JSONObject obj = new JSONObject();
        obj.put(REQUEST_LOCATION_LATITUDE, loc.getLatitude());
        obj.put(REQUEST_LOCATION_LONGITUDE, loc.getLongitude());
        obj.put(REQUEST_LOCATION_STREET_ADDRESS, loc.getStreetAddress());
        obj.put(REQUEST_LOCATION_APPARTMENT_NUMBER, loc.getApartmentNum());
        obj.put(REQUEST_LOCATION_CITY, loc.getCity());
        obj.put(REQUEST_LOCATION_STATE, loc.getState());
        obj.put(REQUEST_LOCATION_ZIP, loc.getZipcode());
        return obj;
    }

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
}