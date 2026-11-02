package com.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest{
    @Test
    public void changeCoordinatesSuccessfully(){
        Location location = new location(34.0000, -81.0000, "123 Main St",0, "Columbia", "SC", "29201");
        
        location.changeCoordinates(35.0000, -82.0000);

        assertEquals(35.0000, location.getLatitude());
        assertEquals(-82.0000, location.getLongitude());

    }

    @Test
    public void allowZeroCoordinates(){
        Location location = new location(34.0000, -81.0000, "123 Main St",0, "Columbia", "SC", "29201");

        location.changeCoordinates(0.0, 0.0);
        assertEquals(0.0, location.getLatitude());
        assertEquals(0.0, location.getLongitude());
    }

    @Test
    public void changeAddressSuccessfully(){
        Location location = new location(34.0000, -81.0000, "123 Main St",0, "Columbia", "SC", "29201");

        location.changeAddress("456 Elm St", 2, "Charleston", "SC", "29401");

        assertEquals("456 Elm St", location.getStreetAddress());
        assertEquals(2, location.getAppartmentNum());
        assertEquals("Charleston", location.getCity());
        assertEquals("SC", location.getState());
        assertEquals("29401", location.getZipcode());

    }

    @Test
    public void allowZeroAppartmentNum(){
        Location location = new location(34.0000, -81.0000, "123 Main St",5, "Columbia", "SC", "29201");

        location.changeAddress("456 Elm St", 0, "Charleston", "SC", "29401");
        assertEquals(0, location.getAppartmentNum());

    }

    @Test
    public void calculateDistanceBetweenLocations(){
        Location location1 = new Location(0.0, 0.0, "123 Main St", 0, "Columbia", "SC", "29201");
        Location location2 = new Location(3.0, 4.0, "456 Elm St", 0, "Charleston", "SC", "29401");

        double distance = location1.calculateDistance(location2);
        assertEquals(5.0, distance, 0.0001);
    }
    
}
