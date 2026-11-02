package com.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


/**
 * Tests for {@link Dataloader}.
 * @author APR
 *
 * +------------------------------------------------+-------------------------------------------------------------------+
 * | Test                                           | Reasoning                                                         |
 * +------------------------------------------------+-------------------------------------------------------------------+
 * | getInstanceTest_new                            | A method to return a list of users coordinator test               |
 * | getInstanceTest_exists                         | A method to return a list of users Requester test                 |
 * | addVolunteerTest_null                          | A method to return a list of users Volunteer test                 |
 * | addVolunteerTest_valid                         | A method to return a list of users not null test                  |
 * | removeVolunteerTest_null                       |
 * | removeVolunteerTest_valid                      |
 * | assignVolunteerTest_null_null                  |
 * | assignVolunteerTest_valid_null                 |
 * | assignVolunteerTest_null_valid                 |
 * | assignVolunteerTest_valid_valid                |
 * +------------------------------------------------+-------------------------------------------------------------------+
 */

/**
 * A class for testing the methods of class Volunteermanager
 * @author APR
 */
public class VolunteerManagerTest {

    private Volunteer volunteer;
    private HelpRequest helpRequest;

    @Before
    public void setup() {
        volunteer = new Volunteer("adam", "smith", UUID.randomUUID(), "asmith@gmail.com", "999-9999", null,
                    null, "asmithy", "hashed5", NotificationType.SMS, new ArrayList<>(Arrays.asList("888-8888")), 
                    true, new ArrayList<>(Arrays.asList("transport")), new ArrayList<Credential>(),
                    new ArrayList<>(Arrays.asList("water")), true, false, 3.25);

        helpRequest = new HelpRequest(UUID.randomUUID(), RequestType.INFRASTRUCTURE, null, "nothing", 
                    AssistanceType.FOOD_SUPPLY, Urgency.MODERATE_PRIORITY, RequestStatus.AWAITING_ASSIGNMENT, 
                    null, "String photo");
    }

    @Test 
    public void getInstanceTest_new() {
        VolunteerManager manager = VolunteerManager.getInstance();

        assertNotNull(manager);
    }

    @Test 
    public void getInstanceTest_exists() {
        VolunteerManager manager1 = VolunteerManager.getInstance();
        VolunteerManager manager2 = VolunteerManager.getInstance();

        assertEquals(manager1, manager2);
    }

    @Test 
    public void addVolunteerTest_null() {
        VolunteerManager.addVolunteer(null);
    }

    @Test 
    public void addVolunteerTest_valid() {
        VolunteerManager.addVolunteer(volunteer);
    }


    @Test 
    public void removeVolunteerTest_null() {
        VolunteerManager.removeVolunteer(null);
    }

    @Test 
    public void removeVolunteerTest_valid() {
        VolunteerManager.removeVolunteer(volunteer);
    }

    @Test 
    public void assignVolunteerTest_null_null() {
        VolunteerManager.assignVolunteer(null, null);
    }

    @Test 
    public void assignVolunteerTest_valid_null() {
        //sets before start
        volunteer.setDispatched(false);
        volunteer.setAvailibility(true);

        VolunteerManager.assignVolunteer(volunteer, null);

        assertFalse(volunteer.isDispatched());
        assertTrue(volunteer.isAvailability());
    }

    @Test 
    public void assignVolunteerTest_null_valid() {
        RequestStatus status = helpRequest.getRequestStatus();
        UUID id = helpRequest.getAssignedVolunteerID();

        VolunteerManager.assignVolunteer(null, helpRequest);

        assertEquals(status, helpRequest.getRequestStatus());
        assertEquals(id, helpRequest.getAssignedVolunteerID());
    }

    @Test 
    public void assignVolunteerTest_valid_valid() {
        //sets before start
        volunteer.setDispatched(false);
        volunteer.setAvailibility(true);

        VolunteerManager.assignVolunteer(volunteer, helpRequest);

        assertTrue(volunteer.isDispatched());
        assertEquals(RequestStatus.ASSIGNED, helpRequest.getRequestStatus());
        assertEquals(volunteer.getUserId(), helpRequest.getAssignedVolunteerID());
    }
    
}