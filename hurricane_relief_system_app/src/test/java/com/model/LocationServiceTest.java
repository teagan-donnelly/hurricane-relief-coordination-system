package com.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Gauvin
 * Tests for the behavior of a {@link LocationService}.
 *
 *
 * +---------------------------------------------------+----------------------------------------------------------------+
 * | Test                                              | Reasoning                                                      |
 * +---------------------------------------------------+----------------------------------------------------------------+
 * | lookingUpRealAddressIsNotSupported                | Looking up a real address is not supported.                    |
 * | lookingUpEmptyAddressIsNotSupported               | Looking up an empty address is not supported.                  |
 * | lookingUpNullAddressIsNotSupported                | Looking up a null address is not supported.                    |
 * | errorMessagePointsToMapTools                      | The exception message must instruct to use MapTools instead.   |
 * +---------------------------------------------------+----------------------------------------------------------------+
 */
public class LocationServiceTest {

    private LocationService service;

    /**
     * Makes a new location service.
     */
    @Before
    public void setUp() {
        service = new LocationService();
    }

    /**
     * Tests that looking up a real address throws an exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void lookingUpRealAddressIsNotSupported() {
        service.getCoordinates("123 Coastal Way, Charleston, SC 29401");
    }

    /**
     * Tests that looking up an empty address throws an exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void lookingUpEmptyAddressIsNotSupported() {
        service.getCoordinates("");
    }

    /**
     * Tests that looking up a missing address throws an exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void lookingUpNullAddressIsNotSupported() {
        service.getCoordinates(null);
    }

    /**
     * Tests that the error message tells the programmer to use MapTools instead.
     */
    @Test
    public void errorMessagePointsToMapTools() {
        try {
            service.getCoordinates("123 Coastal Way, Charleston, SC 29401");
            fail("An exception should have been thrown.");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("MapTools"));
        }
    }
}
