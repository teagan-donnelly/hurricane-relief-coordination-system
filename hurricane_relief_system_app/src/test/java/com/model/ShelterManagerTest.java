package com.model;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Test;

/**
 * Test class to verify how ShelterManager picks shelters.
 */
public class ShelterManagerTest {

    /**
     * Makes a test location in Charleston.
     * @param longitude Longitude of the location
     * @return Test location
     */
    private Location location(double longitude) {
        return new Location(32.78, longitude, "Sample Street", 0, "Charleston", "SC", "29401");
    }

    /**
     * Makes a test request for a family of 3 with a pet and a child.
     * @return Test request
     */
    private PersonalRequest request() {
        return new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, null,
            "Transport and medical assistance", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.AWAITING_ASSIGNMENT, location(-79.93), "", 3, "", 1, 1);
    }

    /**
     * Makes a test shelter.
     * @param name Name of the shelter
     * @param longitude Longitude of the shelter
     * @param occupancy Number of people in the shelter now
     * @param pets true if pets are allowed
     * @param medical true if the shelter has medical staff
     * @return Test shelter
     */
    private Shelter shelter(String name, double longitude, int occupancy,
                            boolean pets, boolean medical) {
        Shelter shelter = new Shelter(name, UUID.randomUUID(), location(longitude), occupancy,
            10, pets, new ArrayList<String>(), true, ShelterStatus.OPEN, medical);
        shelter.setPetSpace(pets ? 2 : 0, 0);
        return shelter;
    }

    /**
     * Tests that shelters that can't fit the family's needs are left out.
     */
    @Test
    public void excludesSheltersThatCannotMeetFamilyNeeds() {
        ShelterManager manager = new ShelterManager();
        assertNotNull(manager.getProblem(shelter("No dogs", -79.94, 0, false, true), request(), true));
        assertNotNull(manager.getProblem(shelter("No medical", -79.94, 0, true, false), request(), true));
        assertNotNull(manager.getProblem(shelter("Two seats", -79.94, 8, true, true), request(), true));
        Shelter fullPets = shelter("Full pets", -79.94, 0, true, true);
        fullPets.setPetSpace(2, 2);
        assertNotNull(manager.getProblem(fullPets, request(), true));
        assertNull(manager.getProblem(shelter("Exact human capacity", -79.94, 7, true, true), request(), true));
    }

    /**
     * Tests that the closest shelter is picked by driving distance, not straight line distance.
     */
    @Test
    public void choosesRoadDistanceInsteadOfStraightLineDistance() throws Exception {
        ArrayList<Shelter> shelters = new ArrayList<>();
        Shelter nearby = shelter("Nearby but longer road", -79.931, 0, true, true);
        Shelter shorterRoad = shelter("Shorter road", -79.96, 0, true, true);
        shelters.add(nearby);
        shelters.add(shorterRoad);
        ShelterManager manager = new ShelterManager() {
            protected double getDrivingDistance(Location start, Location end) {
                return end.getLongitude() == -79.931 ? 6000 : 3000;
            }
        };
        assertSame(shorterRoad, manager.findClosestSuitableShelter(request(), true));
    }

    /**
     * Tests that an exception is thrown when the driving distance lookup fails.
     */
    @Test(expected = IOException.class)
    public void doesNotGuessWhenRoadLookupFails() throws Exception {
        ArrayList<Shelter> shelters = new ArrayList<>();
        shelters.add(shelter("Suitable", -79.94, 0, true, true));
        ShelterManager manager = new ShelterManager() {
            protected double getDrivingDistance(Location start, Location end) throws IOException {
                throw new IOException("Unavailable");
            }
        };
        manager.findClosestSuitableShelter(request(), true);
    }
}
