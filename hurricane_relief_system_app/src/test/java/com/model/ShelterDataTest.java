package com.model;

import static org.junit.Assert.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ShelterDataTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Shelter shelter(UUID admin) {
        Location location = new Location(32.792, -79.9512, "244 President Street",
            0, "Charleston", "SC", "29403");
        Shelter shelter = new Shelter("Sample shelter", UUID.randomUUID(), location,
            5, 20, true, new ArrayList<String>(), true, ShelterStatus.OPEN, true, admin);
        shelter.setPetSpace(5, 0);
        return shelter;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Shelter> reload(JSONObject record) throws Exception {
        JSONArray records = new JSONArray();
        records.add(record);
        Path file = folder.newFile().toPath();
        Files.writeString(file, records.toJSONString(), StandardCharsets.UTF_8);
        return DataLoader.getShelters(file.toString());
    }

    @Test
    public void savesAndReloadsCheckInAndManager() throws Exception {
        UUID admin = UUID.randomUUID();
        Shelter original = shelter(admin);
        PersonalRequest request = new PersonalRequest(UUID.randomUUID(), RequestType.PERSONAL,
            null, "Transportation", AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.ASSIGNED, original.getLocation(), "", 3, "", 1, 1);
        request.addHouseholdMember("Michael Carter, age 39");
        request.addHouseholdMember("Emily Carter, age 37");
        request.addHouseholdMember("Sophie Carter, age 8");
        request.addPet("Buddy (dog)");

        assertTrue(original.checkIn(request));
        ArrayList<Shelter> loaded = reload(DataWriter.getShelterJSON(original));
        assertEquals(1, loaded.size());
        Shelter saved = loaded.get(0);
        assertEquals(admin, saved.getAdmin());
        assertEquals(original.getId(), saved.getId());
        assertEquals(8, saved.getCurrentOccupancy());
        assertEquals(1, saved.getCurrentPetOccupancy());
        assertEquals(4, saved.getAvailablePetSpace());
        assertEquals(original.getOccupants(), saved.getOccupants());
        assertEquals(original.getPets(), saved.getPets());
        assertFalse(saved.checkIn(request));
        assertEquals(8, saved.getCurrentOccupancy());
    }

    @Test
    public void loadsLegacyShelterWithoutOptionalFields() throws Exception {
        JSONObject record = DataWriter.getShelterJSON(shelter(null));
        record.remove(AppConstants.SHELTER_ADMIN);
        record.remove(AppConstants.SHELTER_OCCUPANTS);
        record.remove(AppConstants.SHELTER_PETS);
        record.remove(AppConstants.SHELTER_PET_CAPACITY);
        record.remove(AppConstants.SHELTER_CURRENT_PET_OCCUPANCY);
        ArrayList<Shelter> loaded = reload(record);
        assertEquals(1, loaded.size());
        Shelter saved = loaded.get(0);
        assertNull(saved.getAdmin());
        assertTrue(saved.getOccupants().isEmpty());
        assertTrue(saved.getPets().isEmpty());
        assertEquals(0, saved.getAvailablePetSpace());
        assertEquals(5, saved.getCurrentOccupancy());
    }

    @Test
    public void serializesCoordinatorRoleAsText() {
        Coordinator coordinator = new Coordinator(CoordinatorRole.SHELTER, "Felix", "Moreno",
            "felix@example.com", "843-555-0189", "felix", "demo", null);
        JSONObject record = DataWriter.getCoordinatorJSON(coordinator);
        assertEquals("SHELTER", record.get(AppConstants.COORDINATOR_ROLE));
        coordinator = new Coordinator(null, "Legacy", "Account", "legacy@example.com",
            "843-555-0101", "legacy", "demo", null);
        assertEquals("", DataWriter.getCoordinatorJSON(coordinator).get(AppConstants.COORDINATOR_ROLE));
    }
}
