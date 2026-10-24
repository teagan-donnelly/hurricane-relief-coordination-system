package com.model;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Test;

public class ShelterManagerTest {

    private Location location(double longitude) {
        return new Location(32.78, longitude, "Sample Street", 0, "Charleston", "SC", "29401");
    }

    private PersonalRequest request() {
        return new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL, null,
            "Transport and medical assistance", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.AWAITING_ASSIGNMENT, location(-79.93), "", 3, "", 1, 1);
    }

    private Shelter shelter(String name, double longitude, int occupancy,
                            boolean pets, boolean medical) {
        Shelter shelter = new Shelter(name, UUID.randomUUID(), location(longitude), occupancy,
            10, pets, new ArrayList<String>(), true, ShelterStatus.OPEN, medical, null);
        shelter.setPetSpace(pets ? 2 : 0, 0);
        return shelter;
    }

    @Test
    public void excludesSheltersThatCannotMeetFamilyNeeds() {
        ShelterManager manager = new ShelterManager(new ArrayList<Shelter>());
        assertNotNull(manager.getProblem(shelter("No dogs", -79.94, 0, false, true), request(), true));
        assertNotNull(manager.getProblem(shelter("No medical", -79.94, 0, true, false), request(), true));
        assertNotNull(manager.getProblem(shelter("Two seats", -79.94, 8, true, true), request(), true));
        Shelter fullPets = shelter("Full pets", -79.94, 0, true, true);
        fullPets.setPetSpace(2, 2);
        assertNotNull(manager.getProblem(fullPets, request(), true));
        assertNull(manager.getProblem(shelter("Exact human capacity", -79.94, 7, true, true), request(), true));
    }

    @Test
    public void choosesRoadDistanceInsteadOfStraightLineDistance() throws Exception {
        ArrayList<Shelter> shelters = new ArrayList<>();
        Shelter nearby = shelter("Nearby but longer road", -79.931, 0, true, true);
        Shelter shorterRoad = shelter("Shorter road", -79.96, 0, true, true);
        shelters.add(nearby);
        shelters.add(shorterRoad);
        ShelterManager manager = new ShelterManager(shelters) {
            protected double getDrivingDistance(Location start, Location end) {
                return end.getLongitude() == -79.931 ? 6000 : 3000;
            }
        };
        assertSame(shorterRoad, manager.findClosestSuitableShelter(request(), true));
    }

    @Test(expected = IOException.class)
    public void doesNotGuessWhenRoadLookupFails() throws Exception {
        ArrayList<Shelter> shelters = new ArrayList<>();
        shelters.add(shelter("Suitable", -79.94, 0, true, true));
        ShelterManager manager = new ShelterManager(shelters) {
            protected double getDrivingDistance(Location start, Location end) throws IOException {
                throw new IOException("Unavailable");
            }
        };
        manager.findClosestSuitableShelter(request(), true);
    }
}
