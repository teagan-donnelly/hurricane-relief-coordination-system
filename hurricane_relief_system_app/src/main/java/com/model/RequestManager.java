package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Nicolas Gauvin
 * Manages the collection of help requests in the hurricane relief system.
 */
public class RequestManager {

    private static RequestManager instance;
    private ArrayList<HelpRequest> requests;

    /**
     * Constructor that loads the requests from the json file.
     */
    private RequestManager() {
        requests = DataLoader.getRequests();
    }

    /**
     * Gets the shared RequestManager, creating it on first use.
     * @return the single RequestManager instance
     */
    public static RequestManager getInstance() {
        if (instance == null) {
            instance = new RequestManager();
        }
        return instance;
    }

    /**
     * Sorts the requests from most urgent to least urgent.
     */
    public void sortRequests() {
        for (int i = 0; i < requests.size() - 1; i++) {
            for (int j = 0; j < requests.size() - 1 - i; j++) {
                if (getRank(requests.get(j)) < getRank(requests.get(j + 1))) {
                    HelpRequest temp = requests.get(j);
                    requests.set(j, requests.get(j + 1));
                    requests.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Adds a request to the manager. If it is a duplicate, it gets merged instead.
     * @param request Help request to add
     */
    public void addRequest(HelpRequest request) {
        if (!validateRequest(request)) {
            System.out.println("Request is missing information and was not added.");
            return;
        }

        for (HelpRequest existing : requests) {
            if (isDuplicate(existing, request)) {
                mergeRequest(existing, request);
                System.out.println("Duplicate request found. Requests were merged.");
                return;
            }
        }

        requests.add(request);
    }

    /**
     * Removes a request from the manager.
     * @param request Help request to remove
     */
    public void removeRequest(HelpRequest request) {
        if (request != null) {
            requests.remove(request);
        }
    }

    /**
     * Checks that a request has the information it needs.
     * @param request Help request to check
     * @return true if the request is valid, false otherwise
     */
    public boolean validateRequest(HelpRequest request) {
        if (request == null) {
            return false;
        }
        return request.getUser() != null
            && request.getAssistanceType() != null
            && request.getLocation() != null;
    }

    /**
     * Checks if two requests are for the same thing.
     * @param existing Request already in the system
     * @param newRequest Request being added
     * @return true if they are duplicates, false otherwise
     */
    private boolean isDuplicate(HelpRequest existing, HelpRequest newRequest) {
        if (existing.getRequestStatus() == RequestStatus.COMPLETED
            || existing.getRequestStatus() == RequestStatus.CANCELED) {
            return false;
        }

        boolean sameUser = existing.getUser().getUserId().equals(newRequest.getUser().getUserId());
        boolean sameType = existing.getAssistanceType() == newRequest.getAssistanceType();
        boolean sameAddress = existing.getLocation().getStreetAddress()
            .equals(newRequest.getLocation().getStreetAddress());

        return sameUser && sameType && sameAddress;
    }

    /**
     * Merges a duplicate request into the existing one.
     * @param existing Request already in the system
     * @param duplicate Duplicate request to merge in
     */
    public void mergeRequest(HelpRequest existing, HelpRequest duplicate) {
        // combine the comments
        String comment = existing.getComment() + " | " + duplicate.getComment();

        // keep the old photo unless there isn't one
        String photo = existing.getPhoto();
        if (photo == null || photo.isEmpty()) {
            photo = duplicate.getPhoto();
        }

        existing.updateRequest(comment, existing.getAssistanceType(), existing.getLocation(), photo);

        // keep the higher urgency
        if (getRank(duplicate) > getRank(existing)) {
            existing.setUrgency(duplicate.getUrgency());
        }
    }

    /**
     * Flags a request so an aid coordinator reviews it again.
     * @param request Help request to flag
     */
    public void flagRequest(HelpRequest request) {
        if (request != null) {
            request.setRequestStatus(RequestStatus.PENDING_REVIEW);
        }
    }

    /**
     * Finds a request by its ID.
     * @param requestID ID of the request
     * @return the request, or null if not found
     */
    public HelpRequest getRequest(UUID requestID) {
        for (HelpRequest request : requests) {
            if (request.getRequestID().equals(requestID)) {
                return request;
            }
        }
        return null;
    }

    /**
     * Gets the list of requests.
     * @return ArrayList of help requests
     */
    public ArrayList<HelpRequest> getRequests() {
        return requests;
    }

    /**
     * Saves the requests to the json file.
     * @return true if successful, false otherwise
     */
    public boolean saveRequests() {
        return DataWriter.saveRequests(requests);
    }

    /**
     * Gets a number for a request's urgency so it can be compared.
     * @param request Help request to check
     * @return urgency as a number, -1 if no urgency is set
     */
    private int getRank(HelpRequest request) {
        if (request.getUrgency() == null) {
            return -1;
        }
        return request.getUrgency().ordinal();
    }
}