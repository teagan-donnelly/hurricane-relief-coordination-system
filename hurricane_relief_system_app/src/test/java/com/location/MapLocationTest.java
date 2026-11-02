package com.location;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests for {@link MapLocation}.
 * @author Jorge Naranjo Jr.
 *
 * 
 * +--------------------------------------+------------------------------------------------------------------------------------------------------------------------------+
 * | Test                                 | Reasoning                                                                                                                    |
 * +--------------------------------------+------------------------------------------------------------------------------------------------------------------------------+
 * | toStringShowsFullAddress             | Volunteers and coordinators read this text to know where to go, so it has to show the full street, city, state, and zip.     |
 * | keepsAddressAndCoordinatesItWasGiven | The map tools use these coordinates for routing, so a location must keep the exact address and coordinates it was made with. |
 * +--------------------------------------+------------------------------------------------------------------------------------------------------------------------------+
 */
public class MapLocationTest {

    /**
     * Tests that a map location prints as its full street address.
     */
    @Test
    public void toStringShowsFullAddress() {
        MapLocation location = new MapLocation(new Address("14 Tradd St", "Charleston", "SC", "29401"),
            new Coordinates(32.7765, -79.9311));
        assertEquals("14 Tradd St, Charleston, SC 29401", location.toString());
    }

    /**
     * Tests that a map location keeps the same address and coordinates it was made with.
     */
    @Test
    public void keepsAddressAndCoordinatesItWasGiven() {
        Address address = new Address("14 Tradd St", "Charleston", "SC", "29401");
        Coordinates coordinates = new Coordinates(32.7765, -79.9311);
        MapLocation location = new MapLocation(address, coordinates);
        assertSame(address, location.getAddress());
        assertSame(coordinates, location.getCoordinates());
    }
}
