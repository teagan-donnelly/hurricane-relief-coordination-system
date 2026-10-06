package com.model;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A class representing a DataLoader
 * @author APR
 */

public class DataLoader extends AppConstants {

    /**
     * A method to get users from json
     * @param fileName the json file to get users from
     * @return returns an arrayList of users
     */

    public static ArrayList<User> getUsers(String fileName) {
        switch (fileName) {
            case AppConstants.COORDINATOR_JSON:
                return getCoordinators();
            case AppConstants.REQUESTER_JSON:
                return getRequesters();
            case AppConstants.VOLUNTEER_JSON:
                return getVolunteers();
            default:
                System.out.println("File could not be loaded");
        }
        return new ArrayList<>();
    }

    /**
     * A method to get helpRequests from json
     * @return returns an arrayList of help requests
     */

    public static ArrayList<HelpRequest> getRequests() {
        ArrayList<HelpRequest> requests = new ArrayList<HelpRequest>();

        try {
            FileReader reader = new FileReader(REQUEST_JSON);
            JSONArray requestsJSON = (JSONArray)new JSONParser().parse(reader);

            for(int i = 0; i < requestsJSON.size(); i++) {
                JSONObject requestJSON = (JSONObject)requestsJSON.get(i);
                
                UUID requestID = UUID.fromString((String) requestJSON.get(REQUEST_ID));
                RequestType requestType = RequestType.valueOf(((String) requestJSON.get(REQUEST_TYPE)).toUpperCase());

                UUID requestUser = UUID.fromString((String) requestJSON.get(REQUEST_USER_ID));
                User user = getUserbyID(requestUser, REQUESTER_JSON);

                String comment = (String) requestJSON.get(REQUEST_COMMENT);
                AssistanceType assistanceType = AssistanceType.valueOf(((String) requestJSON.get(REQUEST_ASSISTANCE_TYPE)).toUpperCase());
                Urgency urgency = Urgency.valueOf(((String) requestJSON.get(REQUEST_URGENCY)).toUpperCase());
                RequestStatus requestStatus = RequestStatus.valueOf(((String) requestJSON.get(REQUEST_STATUS)).toUpperCase());
                Location location = getLocation(requestJSON);
                String photo = (String) requestJSON.get(REQUETS_PHOTO);

                switch(requestType) {
                    
                    case PERSONAL:
                        int householdSize = ((Number) requestJSON.get(REQUEST_HOUSEHOLD_SIZE)).intValue();
                        String accessInstructions = (String) requestJSON.get(REQUEST_ACCESS_INTRUCTIONS);

                        requests.add(new PersonalRequest(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo, householdSize, accessInstructions));
                        break;

                    case INFRASTRUCTURE:
                        HazardType hazardType = HazardType.valueOf(((String) requestJSON.get(REQUEST_HAZARD_TYPE)).toUpperCase());
                        LocalDateTime reportTime = LocalDateTime.parse((String) requestJSON.get(REQUEST_REPORT_TIME));

                        requests.add(new InfrustructureRequest(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo, hazardType, reportTime));
                        break;
                    
                    default:
                        requests.add(new HelpRequest(requestID, requestType, user, comment, assistanceType, urgency, requestStatus, location, photo));
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    /**
     * A method to get hurricane events from json
     * @return returns an arraylist of hurricane events
     */
    public static ArrayList<HurricaneEvent> getHurricaneEvents() {
        ArrayList<HurricaneEvent> hurricaneEvents = new ArrayList<HurricaneEvent>();

        try {
            FileReader reader = new FileReader(HURICANE_JSON);
            JSONArray hurricanesJSON = (JSONArray)new JSONParser().parse(reader);

            for(int i = 0; i < hurricanesJSON.size(); i++) {
                JSONObject hurricaneJSON = (JSONObject) hurricanesJSON.get(i);
                
                UUID hurricaneID = UUID.fromString((String) hurricaneJSON.get(HURRICANE_ID));
                String name = (String) hurricaneJSON.get(HURRICANE_NAME);
                int category = ((Number) hurricaneJSON.get(HURRICANE_CATEGORY)).intValue();
                HurricaneStatus status = HurricaneStatus.valueOf(((String) hurricaneJSON.get(HURRICANE_STATUS)).toUpperCase());

                ArrayList<String> affectedZipcodes = getJSONList(hurricaneJSON, HURRICANE_AFFECTED_ZIPCODES);

                LocalDateTime startDate = LocalDateTime.parse((String) hurricaneJSON.get(HURRICANE_START_DATE));
                LocalDateTime endDate = LocalDateTime.parse((String) hurricaneJSON.get(HURRICANE_END_DATE));
                String description = (String) hurricaneJSON.get(HURRICANE_DESCRIPTION);

                
                hurricaneEvents.add(new HurricaneEvent(hurricaneID, name, category, status, affectedZipcodes, startDate, endDate, description));
                        
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hurricaneEvents;
    }


    
    /**
     * A helper method for getUser to return coordinators
     * @return returns an arrayList of coordinators
     */
    private static ArrayList<User> getCoordinators() {
        ArrayList<User> users = new ArrayList<User>();

        try {
            FileReader reader = new FileReader(COORDINATOR_JSON);
            JSONArray coordinatorsJSON = (JSONArray)new JSONParser().parse(reader);

            for(int i = 0; i < coordinatorsJSON.size(); i++) {
                JSONObject coordinatorJSON = (JSONObject)coordinatorsJSON.get(i);
                UserData data = getUserData(coordinatorJSON);

                users.add(new AidCoordinator(data.firstName, data.lastName, data.id, data.email, data.location, data.birthDate, 
                    data.userName, data.password, data.notificationPreference, data.emergencyContacts, null));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * A helper method for getUuser to return aid requesters
     * @return returns an arrayList of coordinators
     */
    private static ArrayList<User> getRequesters() {
        ArrayList<User> users = new ArrayList<User>();

        try {
            FileReader reader = new FileReader(REQUESTER_JSON);
            JSONArray requestersJSON = (JSONArray)new JSONParser().parse(reader);

            for(int i = 0; i < requestersJSON.size(); i++) {
                JSONObject requesterJSON = (JSONObject)requestersJSON.get(i);
                UserData data = getUserData(requesterJSON);

                int householdSize = ((Number) requesterJSON.get(USER_HOUSEHOLDSIZE)).intValue();
                AssistanceType assistanceType = AssistanceType.valueOf(((String) requesterJSON.get(USER_ASSISTANCE_TYPE)).toUpperCase());

                ArrayList<String> pastRequests = getJSONList(requesterJSON, USER_PAST_REQUESTS);

                users.add(new AidRequester(data.firstName, data.lastName, data.id, data.email, data.location, data.birthDate, 
                    data.userName, data.password, data.notificationPreference, data.emergencyContacts, householdSize, assistanceType, pastRequests));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * A helper method for getUser to return volunteers
     * @return returns an arrayList of coordinators
     */
    private static ArrayList<User> getVolunteers() {
        ArrayList<User> users = new ArrayList<User>();

        try {
            FileReader reader = new FileReader(VOLUNTEER_JSON);
            JSONArray volunteersJSON = (JSONArray)new JSONParser().parse(reader);

            for(int i = 0; i < volunteersJSON.size(); i++) {
                JSONObject volunteerJSON = (JSONObject)volunteersJSON.get(i);
                UserData data = getUserData(volunteerJSON);

                boolean availability = (boolean)volunteerJSON.get(USER_AVAILIBILITY);
                double travelDistance = ((Number)volunteerJSON.get(USER_TRAVEL_DISTANCE)).doubleValue();

                ArrayList<String> userSkills = getJSONList(volunteerJSON, USER_SKILLS);

                ArrayList<Credential> userCredentials = new ArrayList<>();
                JSONArray credentials = (JSONArray)volunteerJSON.get(USER_CREDENTIALS);
                if (credentials != null) {
                    for (Object credential : credentials) {
                        JSONObject credentialJSON = (JSONObject) credential;

                        CredentialType type = CredentialType.valueOf(((String) credentialJSON.get(USER_CREDENTIALS_TYPE)).toUpperCase());
                        String status = (String) credentialJSON.get(USER_CREDENTIALS_STATUS);

                        userCredentials.add(new Credential(type, status));
                    }
                }

                ArrayList<String> userSupplies = getJSONList(volunteerJSON, USER_SUPPLIES);

                boolean isVerified = (boolean)volunteerJSON.get(USER_VERIFIED);
                boolean isDispached = (boolean)volunteerJSON.get(USER_DISPACHED);
                double volunteerHours = ((Number) volunteerJSON.get(USER_VOLUNTEER_HOURS)).doubleValue();

                users.add(new Volunteer(data.firstName, data.lastName, data.id, data.email, data.location, data.birthDate, 
                    data.userName, data.password, data.notificationPreference, data.emergencyContacts, availability, 
                    userSkills, userCredentials, userSupplies, isVerified, isDispached, volunteerHours));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    //helpers to cut down repetitive code
    /**
     * A class for shared user data
     */
    private static class UserData {
        UUID id;
        String firstName;
        String lastName;
        String email;
        Location location;
        String userName;
        String password;
        LocalDate birthDate;
        NotificationType notificationPreference;
        ArrayList<String> emergencyContacts;
    }

    /**
     * A helper method to build a user
     * @param userJSON the user object from json to build from
     * @return returns UserData type
     */
    private static UserData getUserData(JSONObject userJSON) {
        UserData data = new UserData();

        data.id = UUID.fromString((String) userJSON.get(USER_ID));
        data.firstName = (String) userJSON.get(USER_FIRST_NAME);
        data.lastName = (String) userJSON.get(USER_LAST_NAME);
        data.email = (String) userJSON.get(USER_EMAIL);
        data.location = getLocation(userJSON);
        data.userName = (String) userJSON.get(USER_USERNAME);
        data.password = (String) userJSON.get(USER_PASSWORD);

        data.birthDate = LocalDate.parse((String) userJSON.get(USER_BIRTHDATE));

        data.notificationPreference = NotificationType.valueOf(((String) userJSON.get(USER_NOIFICATION_PREFEREMCE)).toUpperCase());
                
        data.emergencyContacts = getJSONList(userJSON, USER_EMERGENCY_CONTACTS);

        return data;
    }

    /**
     * A helper method to get information from a json list
     * @param jsonObject the json object to look through
     * @param find the list to iterate through
     * @return returns an arraylist of the json list contents
     */
    private static ArrayList<String> getJSONList(JSONObject jsonObject, String find) {
        ArrayList<String> arrayList = new ArrayList<>();
        JSONArray items = (JSONArray)jsonObject.get(find);
        if (items != null) {
            for (Object item : items) {
                arrayList.add((String) item);
            }
        }

        return arrayList;
    }

    /**
     * A helper method to find a user by their UUID
     * @param id the UUID to look for
     * @param userFile the file to look through
     * @return returns the user found or null on failure to find
     */
    private static User getUserbyID(UUID id, String userFile) {
        ArrayList<User> users = getUsers(userFile);

        for (User user : users) {
            if (user.getUserId().equals(id)) return user;
        }

        return null;
    }

    /**
     * A helper method to construct the location
     * @param object the Json object to read for location
     * @return returns a new location object
     */
    private static Location getLocation(JSONObject object) {
        JSONObject locationObject = (JSONObject) object.get(REQUEST_LOCATION);
        if (locationObject != null) {
            double latitude = ((Number) locationObject.get(REQUEST_LOCATION_LATITUDE)).doubleValue();
            double longitude = ((Number) locationObject.get(REQUEST_LOCATION_LONGITUDE)).doubleValue();
            String streetAddress = (String) locationObject.get(REQUEST_LOCATION_STREET_ADDRESS);
            int apartmanentNumber = ((Number) locationObject.get(REQUEST_LOCATION_APPARTMENT_NUMBER)).intValue();
            String city = (String) locationObject.get(REQUEST_LOCATION_CITY);
            String state = (String) locationObject.get(REQUEST_LOCATION_STATE);
            String zipcode = (String) locationObject.get(REQUEST_LOCATION_ZIP);

            return new Location(latitude, longitude, streetAddress, apartmanentNumber, city, state, zipcode);
        }
        return null;
    }

    public static void main(String[] args) {
        ArrayList<HurricaneEvent> requests = getHurricaneEvents();

		for(HurricaneEvent request : requests){
			System.out.println(request);
		}
    }

}

