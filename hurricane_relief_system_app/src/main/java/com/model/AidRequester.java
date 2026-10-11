package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
public class AidRequester extends User {

    private int householdSize;
    private AssistanceType assistanceType;
    private ArrayList<String> pastRequests;

    public AidRequester (String firstName, String lastName, UUID userID, String email,
                Location location, LocalDate birthDate, String userName, String password,
                NotificationType notificationPreference, ArrayList<String> emergencyContacts,
            int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {

        super(firstName, lastName, userID, email, location, birthDate, userName, password,
            notificationPreference, emergencyContacts);

        this.householdSize = householdSize;
        this.assistanceType = assistanceType;
        loadRequests(pastRequests);
    }

    public AidRequester(int householdSize, AssistanceType assistanceType, ArrayList<String> pastRequests) {
        super("", "", UUID.randomUUID(), "", null, LocalDate.now(), "", "", null, new ArrayList<>());
        this.householdSize = householdSize;
        this.assistanceType = assistanceType;
        loadRequests(pastRequests);
    }

    /**
     * Submits a personal help request, for this requester or for someone else.
     * @param firstName first name of the person who needs help
     * @param lastName last name of the person who needs help
     * @param email email of the person who needs help
     * @param address street address where help is needed, or "" for this requester's address
     * @param householdSize number of people at the address
     * @param assistanceType the kind of help needed
     */
    public void makeRequest(String firstName, String lastName, String email, String address, int householdSize, AssistanceType assistanceType) {
        if (assistanceType == null || householdSize < 1) {
            return;
        }

        Location location = getLocation();
        if (address != null && !address.isEmpty()) {
            // TODO: get real coordinates from LocationService once it works
            location = new Location(0, 0, address, 0, "", "", "");
        }

        String comment = "Help needed for " + firstName + " " + lastName + " (" + email + ")";
        PersonalRequest request = new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, this, comment, assistanceType, Urgency.MODERATE_PRIORITY, RequestStatus.PENDING_REVIEW, location, "", householdSize, "");

        ArrayList<HelpRequest> helpRequests = DataLoader.getRequests();
        helpRequests.add(request);
        DataWriter.saveRequests(helpRequests);

        ArrayList<String> newRequest = new ArrayList<>();
        newRequest.add(request.getRequestID().toString());
        addRequest(newRequest);
    }

    /**
     * Prints this requester's requests. Pass 0 and null to see all of them.
     * @param householdSize only show requests for this household size, or 0 for any
     * @param assistanceType only show requests of this type, or null for any
     */
    private void viewRequests(int householdSize, AssistanceType assistanceType) {
        for (HelpRequest request : DataLoader.getRequests()) {
            if (!pastRequests.contains(request.getRequestID().toString())) {
                continue;
            }
            if (assistanceType != null && request.getAssistanceType() != assistanceType) {
                continue;
            }
            if (householdSize > 0 && request instanceof PersonalRequest && ((PersonalRequest) request).getHouseholdSize() != householdSize) {
                continue;
            }
            System.out.println(request.getRequestID() + "  " + request.getAssistanceType()
                + "  " + request.getUrgency() + "  " + request.getRequestStatus());
        }
    }

    /**
     * Adds request IDs to this requester's history, skipping any already there.
     * @param pastRequests the request IDs to add
     */
    private void addRequest(ArrayList<String> pastRequests) {
        if (pastRequests == null) {
            return;
        }
        for (String requestID : pastRequests) {
            if (!this.pastRequests.contains(requestID)) {
                this.pastRequests.add(requestID);
            }
        }
    }

    /**
     * Replaces this requester's history with the request IDs loaded from JSON.
     * @param pastRequests the request IDs to load
     */
    private void loadRequests(ArrayList<String> pastRequests) {
        this.pastRequests = new ArrayList<>();
        addRequest(pastRequests);
    }

    /**
     * Saves changes to one of this requester's active requests.
     * @param helpRequest the request with its updated details
     */
    public void editRequest(HelpRequest helpRequest) {
        if (helpRequest == null || !pastRequests.contains(helpRequest.getRequestID().toString())) {
            return;
        }

        ArrayList<HelpRequest> requests = DataLoader.getRequests();
        for (int i = 0; i < requests.size(); i++) {
            HelpRequest saved = requests.get(i);
            if (saved.getRequestID().equals(helpRequest.getRequestID())) {
                RequestStatus status = saved.getRequestStatus();
                if (status == RequestStatus.COMPLETED || status == RequestStatus.CANCELED || status == RequestStatus.DENIED) {
                    return;
                }
                requests.set(i, helpRequest);
                DataWriter.saveRequests(requests);
                return;
            }
        }
    }

    /**
     * Removes one of this requester's requests from the saved requests.
     * @param helpRequest the request to delete
     */
    public void deleteRequest(HelpRequest helpRequest) {
        if (helpRequest == null || !pastRequests.contains(helpRequest.getRequestID().toString())) {
            return;
        }

        ArrayList<HelpRequest> requests = DataLoader.getRequests();
        for (HelpRequest saved : requests) {
            if (saved.getRequestID().equals(helpRequest.getRequestID())) {
                requests.remove(saved);
                DataWriter.saveRequests(requests);
                return;
            }
        }
    }

    public int getHouseholdSize() {
        return householdSize;
    }

    public AssistanceType getAssistanceType() {
        return assistanceType;
    }

    public ArrayList<String> getPastRequests() {
        return pastRequests;
    }
}
