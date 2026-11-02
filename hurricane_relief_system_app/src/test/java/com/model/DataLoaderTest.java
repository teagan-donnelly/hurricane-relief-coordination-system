package com.model;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;


/**
 * Tests for {@link Dataloader}.
 * @author APR
 *
 * +------------------------------------------------+-------------------------------------------------------------------+
 * | Test                                           | Reasoning                                                         |
 * +------------------------------------------------+-------------------------------------------------------------------+
 * | getAllUsers_testCoordinator                    | A method to return a list of users coordinator test               |
 * | getAllUsers_testRequester                      | A method to return a list of users Requester test                 |
 * | getAllUsers_testVolunteer                      | A method to return a list of users Volunteer test                 |
 * | getAllUsers_testNotEmpty                       | A method to return a list of users not null test                  |
 * |                                                |                                                                   |
 * | getRequestsTest_head                           | Tests if the first emement can be retrieved                       |
 * | getRequestsTest_tail                           | Tests if the last emement can be retrieved                        |
 * |                                                |                                                                   |
 * | getHurricaneEventsTest_head                    | Tests if the first emement can be retrieved                       |
 * | getHurricaneEventsTest_tail                    | Tests if the last emement can be retrieved                        |
 * |                                                |                                                                   |
 * | getSheltersTest_head                           | Tests if the first emement can be retrieved                       |
 * | getSheltersTest_tail                           | Tests if the last emement can be retrieved                        |
 * |                                                |                                                                   |
 * | getCoordinatorsTest_head                       | Tests if the first emement can be retrieved                       |
 * | getCoordinatorsTest_tail                       | Tests if the last emement can be retrieved                        |
 * |                                                |                                                                   |
 * | getRequestersTest_head                         | Tests if the first emement can be retrieved                       |
 * | getRequestersTest_tail                         | Tests if the last emement can be retrieved                        |
 * |                                                |                                                                   |
 * | getVolunteersTest_head                         | Tests if the first emement can be retrieved                       |
 * | getVolunteersTest_tail                         | Tests if the last emement can be retrieved                        |
 * |                                                |                                                                   |
 * | getUserbyIDTest_exists                         | tests if a user can be fetched by id                              |
 * | getUserbyIDTest_fail                           | tests if a false id returns no user                               |
 * +------------------------------------------------+-------------------------------------------------------------------+
 */

/**
 * A class for testing the methods of class DataLoader
 * @author APR
 */
public class DataLoaderTest {

    @Test
    public void getAllUsers_testCoordinator() {
        ArrayList<User> users = DataLoader.getAllUsers();

        assertFalse(users.isEmpty());
        UUID id = UUID.fromString("e749d25b-831a-4c60-a219-75b3f9d08642");
        for (User user : users) {
            if (user.getUserId().equals(id)) assertTrue(user.getUserId().equals(id));
        }
    }

    @Test
    public void getAllUsers_testRequester() {
        ArrayList<User> users = DataLoader.getAllUsers();

        assertFalse(users.isEmpty());
        UUID id = UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d");
        for (User user : users) {
            if (user.getUserId().equals(id)) assertTrue(user.getUserId().equals(id));
        }
    }

    @Test
    public void getAllUsers_testVolunteer() {
        ArrayList<User> users = DataLoader.getAllUsers();

        assertFalse(users.isEmpty());
        UUID id = UUID.fromString("42d971a6-f83b-4c15-9e60-7a203bd584fc");
        for (User user : users) {
            if (user.getUserId().equals(id)) assertTrue(user.getUserId().equals(id));
        }
    }

    @Test
    public void getAllUsers_testNotEmpty() {
        ArrayList<User> users = DataLoader.getAllUsers();
        assertFalse(users.isEmpty());
    }

    @Test 
    public void getRequestsTest_head() {
        ArrayList<HelpRequest> request = DataLoader.getRequests();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("139a95f0-c8a5-4d66-a8c8-b737f99ad142");
        assertEquals(request.get(0).getRequestID(), id);
    }

    @Test 
    public void getRequestsTest_tail() {
        ArrayList<HelpRequest> request = DataLoader.getRequests();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("0c57a3e8-2f90-46b4-9d61-e8a72c5f1039");
        assertEquals(request.get(request.size() - 1).getRequestID(), id);
    }


    @Test
    public void getHurricaneEventsTest_head() {
        ArrayList<HurricaneEvent> hurricane = DataLoader.getHurricaneEvents();
        assertFalse(hurricane.isEmpty());
        UUID id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        assertEquals(hurricane.get(0).getHuricaneID(), id);
    }

    @Test
    public void getHurricaneEventsTest_tail() {
        ArrayList<HurricaneEvent> hurricane = DataLoader.getHurricaneEvents();
        assertFalse(hurricane.isEmpty());
        UUID id = UUID.fromString("e26b7c90-3a54-48df-b612-9f0d5a83c741");
        assertEquals(hurricane.get(hurricane.size() - 1).getHuricaneID(), id);
    }


    @Test
    public void getSheltersTest_head() {
        ArrayList<Shelter> request = DataLoader.getShelters();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("f95ca0de-005d-4258-a5ba-ad82f2fc78ca");
        assertEquals(request.get(0).getId(), id);
    }

    @Test
    public void getSheltersTest_tail() {
        ArrayList<Shelter> request = DataLoader.getShelters();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("40928a02-c346-4627-98a7-6a414c76f2be");
        assertEquals(request.get(request.size() - 1).getId(), id);
    }


    @Test
    public void getCoordinatorsTest_head() {
        ArrayList<Coordinator> request = DataLoader.getCoordinators();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("e749d25b-831a-4c60-a219-75b3f9d08642");
        assertEquals(request.get(0).getUserId(), id);
    }

    @Test
    public void getCoordinatorsTest_tail() {
        ArrayList<Coordinator> request = DataLoader.getCoordinators();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("9b473d60-a815-42e9-967c-3f04b2d18a75");
        assertEquals(request.get(request.size() - 1).getUserId(), id);
    }


    @Test
    public void getRequestersTest_head() {
        ArrayList<AidRequester> request = DataLoader.getRequesters();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d");
        assertEquals(request.get(0).getUserId(), id);
    }

    @Test
    public void getRequestersTest_tail() {
        ArrayList<AidRequester> request = DataLoader.getRequesters();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("83c5d1a7-604e-4f92-b368-e2a79d105fc4");
        assertEquals(request.get(request.size() - 1).getUserId(), id);
    }


    @Test
    public void getVolunteersTest_head() {
        ArrayList<Volunteer> request = DataLoader.getVolunteers();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("42d971a6-f83b-4c15-9e60-7a203bd584fc");
        assertEquals(request.get(0).getUserId(), id);
    }

    @Test
    public void getVolunteersTest_tail() {
        ArrayList<Volunteer> request = DataLoader.getVolunteers();
        assertFalse(request.isEmpty());
        UUID id = UUID.fromString("5c82a703-e149-46bd-8f20-a674d93b015e");
        assertEquals(request.get(request.size() - 1).getUserId(), id);   
    }


    @Test
    public void getUserbyIDTest_exists() {
        UUID id = UUID.fromString("7e56c218-0a93-4d67-b825-f1396c04a7de");
        User user = DataLoader.getUserbyID(id);
        assertNotNull(user);
    }

    @Test
    public void getUserbyIDTest_fail() {
        UUID id = UUID.fromString("7e56c218-0a93-4d67-b825-f1396c04a7dd");
        User user = DataLoader.getUserbyID(id);
        assertNull(user);
    }


    
}