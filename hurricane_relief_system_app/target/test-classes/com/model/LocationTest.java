package com.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest{
    @Test
    public void changeCoordinatesSuccessfully(){
        Location location = new location(34.0000, -81.0000, "123 Main St", "Columbia", "SC", "29201");
        
        location.changeCoordinates(35.0000, -82.0000);

        assertEquals(35.0000, location.getLatitude());
        assertEquals(-82.0000, location.getLongitude());

    }
    
}
