package com.model;

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the behavior of a {@link PersonalRequest}.
 *
 * +-----------------------------------------------------+-------------------------------------------------------------------------------------------------------+
 * | Test                                                | Reasoning                                                                                             |
 * +-----------------------------------------------------+-------------------------------------------------------------------------------------------------------+
 * | fullConstructorKeepsFamilyDetails                   | The full constructor should keep the counts and access instructions needed to plan a pickup.          |
 * | shortConstructorKeepsFamilyDetails                  | Leaving out the optional request details should still keep the family's transportation needs.         |
 * | shortConstructorUsesPersonalRequestType             | A personal request should identify itself as PERSONAL even when the type is not a constructor input.  |
 * | requestWithoutPetsOrChildrenKeepsZeroCounts         | A household without pets or children is a valid boundary case and should not gain either count.       |
 * | addingHouseholdMemberStoresMember                   | The pickup and shelter records need to include the household member who was added.                    |
 * | addingAnotherHouseholdMemberKeepsExistingMembers    | Adding the rest of a family should keep every member in the household list.                           |
 * | addingPetStoresPet                                  | Buddy's name and species should be available when volunteers review the request.                      |
 * | addingAnotherPetKeepsExistingPets                   | A household bringing multiple pets should not lose an earlier pet when another is added.              |
 * | householdMembersAreSeparateForEachRequest           | Adding a member to one family's request should not change another family's request.                   |
 * | petsAreSeparateForEachRequest                       | Pets on one request should not appear on another request.                                             |
 * +-----------------------------------------------------+-------------------------------------------------------------------------------------------------------+
 */
public class PersonalRequestTest {

    private Location carterHome;
    private AidRequester requester;
    private PersonalRequest request;

    /**
     * Sets up a requester and a request for three people, one pet, and one child.
     */
    @Before
    public void setUp() {
        carterHome = new Location(32.7765, -79.9311, "14 Tradd St", 0,
            "Charleston", "SC", "29401");
        requester = new AidRequester("Michael", "Carter",
            UUID.fromString("00000000-0000-4000-8000-000000000001"), "carter@example.com",
            "843-555-0147", carterHome, LocalDate.of(1987, 4, 12), "michaelcarter", "demo123",
            NotificationType.EMAIL, new ArrayList<String>(), 3,
            AssistanceType.TRANSPORT, new ArrayList<String>());
        request = request(3, 1, 1);
    }

    /**
     * Makes a personal request with the given household counts.
     * @param householdSize Number of people needing transportation
     * @param pets Number of pets coming with the household
     * @param children Number of children in the household
     * @return Test request
     */
    private PersonalRequest request(int householdSize, int pets, int children) {
        return new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, requester,
            "Need transport to a shelter with medical staff", AssistanceType.TRANSPORT,
            Urgency.URGENT_PRIORITY, RequestStatus.AWAITING_ASSIGNMENT, carterHome,
            "street.jpg", householdSize, "Use the side entrance", pets, children);
    }

    /**
     * Tests that the full constructor keeps the household counts and access instructions.
     */
    @Test
    public void fullConstructorKeepsFamilyDetails() {
        assertEquals(3, request.getHouseholdSize());
        assertEquals("Use the side entrance", request.getAccessInstructions());
        assertEquals(1, request.getPets());
        assertEquals(1, request.getChildren());
    }

    /**
     * Tests that the shorter constructor also keeps the household details.
     */
    @Test
    public void shortConstructorKeepsFamilyDetails() {
        PersonalRequest shortRequest = new PersonalRequest(UUID.randomUUID(), requester,
            AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY, RequestStatus.AWAITING_ASSIGNMENT,
            carterHome, 3, "Use the side entrance", 1, 1);
        assertEquals(3, shortRequest.getHouseholdSize());
        assertEquals("Use the side entrance", shortRequest.getAccessInstructions());
        assertEquals(1, shortRequest.getPets());
        assertEquals(1, shortRequest.getChildren());
    }

    /**
     * Tests that the shorter constructor identifies the request as personal.
     */
    @Test
    public void shortConstructorUsesPersonalRequestType() {
        PersonalRequest shortRequest = new PersonalRequest(UUID.randomUUID(), requester,
            AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY, RequestStatus.AWAITING_ASSIGNMENT,
            carterHome, 3, "Use the side entrance", 1, 1);
        assertEquals(RequestType.PERSONAL, shortRequest.getRequestType());
    }

    /**
     * Tests that a household can have no pets and no children.
     */
    @Test
    public void requestWithoutPetsOrChildrenKeepsZeroCounts() {
        PersonalRequest adultsOnly = request(2, 0, 0);
        assertEquals(0, adultsOnly.getPets());
        assertEquals(0, adultsOnly.getChildren());
    }

    /**
     * Tests that adding a household member stores the name and age.
     */
    @Test
    public void addingHouseholdMemberStoresMember() {
        request.addHouseholdMember("Sophie Carter, age 8");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Sophie Carter, age 8");
        assertEquals(expected, request.getHouseholdMembers());
    }

    /**
     * Tests that adding more household members keeps the whole list.
     */
    @Test
    public void addingAnotherHouseholdMemberKeepsExistingMembers() {
        request.addHouseholdMember("Michael Carter, age 39");
        request.addHouseholdMember("Emily Carter, age 37");
        request.addHouseholdMember("Sophie Carter, age 8");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Michael Carter, age 39");
        expected.add("Emily Carter, age 37");
        expected.add("Sophie Carter, age 8");
        assertEquals(expected, request.getHouseholdMembers());
    }

    /**
     * Tests that adding a pet stores the name and species.
     */
    @Test
    public void addingPetStoresPet() {
        request.addPet("Buddy (dog)");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Buddy (dog)");
        assertEquals(expected, request.getPetNames());
    }

    /**
     * Tests that adding another pet keeps the pets already on the request.
     */
    @Test
    public void addingAnotherPetKeepsExistingPets() {
        PersonalRequest twoPets = request(3, 2, 1);
        twoPets.addPet("Buddy (dog)");
        twoPets.addPet("Luna (cat)");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Buddy (dog)");
        expected.add("Luna (cat)");
        assertEquals(expected, twoPets.getPetNames());
    }

    /**
     * Tests that different requests do not share their household member lists.
     */
    @Test
    public void householdMembersAreSeparateForEachRequest() {
        PersonalRequest otherRequest = request(1, 0, 0);
        otherRequest.addHouseholdMember("Alex Taylor, age 30");
        assertTrue(request.getHouseholdMembers().isEmpty());
    }

    /**
     * Tests that different requests do not share their pet lists.
     */
    @Test
    public void petsAreSeparateForEachRequest() {
        PersonalRequest otherRequest = request(1, 1, 0);
        otherRequest.addPet("Luna (cat)");
        assertTrue(request.getPetNames().isEmpty());
    }
}
