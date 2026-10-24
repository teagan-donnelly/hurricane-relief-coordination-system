package com.model;

/**
 * Old location service. Use com.location.MapTools instead.
 */
public class LocationService{
    private String API_KEY;
    
    /**
     * Gets the coordinates of an address. This is no longer supported.
     * @param addressText Address to look up
     * @return Nothing, always throws an exception
     */
    public double[] getCoordinates(String addressText){
        throw new UnsupportedOperationException("Use com.location.MapTools for location lookups.");
    }

}
    
