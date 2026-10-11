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
        super("", "", UUID.randomUUID(), "", null, LocalDate.now(), "", "", NotificationType.NONE, new ArrayList<>());
        this.householdSize = householdSize;
        this.assistanceType = assistanceType;
        loadRequests(pastRequests);
    }

   public void makeRequest(String firstName, String lastName, String email, String address, int householdSize, AssistanceType assistanceType) {
       if (assistanceType == null || householdSize < 1) {
        return;
       }

       String comment = "Help needed for " + firstName + " " + lastName + " (" + email + ") at " + address + ") for household size " + householdSize + " needing " + assistanceType;
       PersonalRequest request = new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, this, comment, assistanceType, Urgency.MODERATE_PRIORITY, RequestStatus.PENDING_REVIEW, location, "", householdSize, "");

       ArrayList<HelpRequest> helpRequests = new ArrayList<>();
       helpRequests.add(request);
       DataWriter.saveRequests(helpRequests);

       ArrayList<String> newRequest = new ArrayList<>();
       newRequest.add(request.getRequestID().toString());
       addRequest(newRequest);

   }

   private void viewRequests() {
    for (String HelpRequest request : DataLoader.getRequests()) {
        if (!pastRequests.contains(request.getRequestID().toString())) {
            continue;
        }
        if (assistanceType != null && request.getAssistanceType() != assistanceType) {
            continue;
        }
        if (householdSize > 0 && request instanceof PersonalRequest && ((PersonalRequest) request).getHouseholdSize() != householdSize) {
            continue;
        }

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

        private void loadRequests(ArrayList<String> pastRequests) {
            this.pastRequests = new ArrayList<>();
            addRequest(pastRequests);
        }

        public void editRequest(HelpRequest helpRequest) {
            if (helpRequest == null || !pastRequests.contains(helpRequest.getRequestID().toString())) {
                return;
            }

        }

        ArrayList<HelpRequest> requests = DataLoader.getRequests();
        for (int i = 0; i < requests.size(); i++) {
            HelpRequest request = requests.get(i);
            if (saved.getRequestID().equals(helpRequest.getRequestID())) {
                RequestStatus status = saved.getRequestStatus();
                if (status == RequestStatus.COMPLETED || status == RequestStatus.CANCELLED || status == RequestStatus.DENIED) {
                    return;
                }
                  requests.set(i, helpRequest);
                  DataWriter.saveRequests(requests);
                  return;
            }
            }
        }
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
