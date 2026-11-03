package com.model;

import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.UUID;
 
public class ShelterTest{
    private Shelter createShelter(){
        return new Shelter("Hurricane Shelter", UUID.randomUUID(), null, 5, 20, true, new ArrayList<>(), true, ShelterStatus.OPEN, true);

    }


    @Test
    public void setValidPetSpace(){
        Shelter shelter = createShelter();
        Shelter.setPetSpace(10,4);
        assertEquals(10, shelter.getPetCapacity());
        assertEquals(4, shelter.getCurrentPetOccupancy());
    }

    @Test
    public void allowZeroPetCapacity(){
        Shelter shelter = createShelter();
        shelter.setPetSpace(0,0);
        assertEquals(0, shelter.getPetCapacity());
    }

    @Test
    public void noNegativePetCapacity(){
        Shelter shelter = createShelter();
        assertThrows(IllegalArgumentException.class, ()-> shelter.setPetSpace(-1,0));
    }

    @Test 
    public void noNegativePetOccupancy(){
        Shelter shelter = createShelter();
        assertThrows(IllegalArgumentException.class, ()-> shelter.setPetSpace(10,-1));
    }

    @Test 
    public void noPetOccupancyGreaterThanCapacity(){
        Shelter shelter = createShelter();
        assertThrows(IllegalArgumentException.class, ()-> shelter.setPetSpace(5,6));
    }

    @Test 
    public void returnCorrectPetSpace(){
        Shelter shelter = createShelter();
        shelter.setPetSpace(10,4);
        assertEquals(6, shelter.getAvailablePetSpace());
    }

    @Test 
    public void returnToZeroWhenPetSpaceIsFull(){
        Shelter shelter = createShelter();
        shelter.setPetSpace(5,5);
        assertEquals(0, shelter.getAvailablePetSpace());
    }

    @Test 
    public void updateShelterCapacity(){
        Shelter shelter = createShelter();
        shelter.setCapacity(30);
        assertEquals(30, shelter.getCapacity());
    }

    @Test 
    public void returnTrueWhenUserIsShelterAdmin(){
        UUID admindId = UUID.randomUUID();

        Shelter shelter = new Shelter("Hurricane Shelter", UUID.randomUUID(), null, 20, admindId);
        User user = new User();
        user.setUserId(admindId);
        assertTrue(shelter.isManagedBy(user));
    }

    @Test
    public void returnFalseWhenUserIsNotShelterAdmin(){
        UUID admindId = UUID.randomUUID();

        Shelter shelter = new Shelter("Hurricane Shelter", UUID.randomUUID(), null, 20, admindId);
        User user = new User();
        user.setUserId(UUID.randomUUID());
        assertFalse(shelter.isManagedBy(user));
    }

    @Test 
    public void falseWhenUserIsNull(){
        Shelter shelter = createShelter();
        assertFalse(shelter.isManagedBy(null));
    }

    @Test 
    public void falseWhenShelterHasNoAdmin(){
        Shelter shelter = new Shelter("Hurricane Shelter", UUID.randomUUID(), null, 20, (UUID) null);
        User user = new User();
        assertFalse(shelter.isManagedBy(user));
    }

    @Test 
    public void addOccupantToShelter(){
        Shelter shelter = createShelter();
        shelter.addOccupant("Cameron Seymore");
        assertTrue(shelter.getOccupants().contains("Cameron Seymore"));
    }

    @Test 
    public void addPetToShelter(){
        Shelter shelter = creatShelter();
        shelter.addPet("Buddy");
        assertTrue(shelter.getPets().contains("Buddy"));
    }


}