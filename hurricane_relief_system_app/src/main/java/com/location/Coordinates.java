package com.location;

/**
 * Represents a point using latitude and longitude.
 */
public class Coordinates {
    private double latitude;
    private double longitude;

    /**
     * Constructor for coordinates.
     * @param latitude Latitude of the point
     * @param longitude Longitude of the point
     */
    public Coordinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the latitude.
     * @return Latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude.
     * @return Longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the coordinates as a string.
     * @return Coordinates in the format latitude, longitude
     */
    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
