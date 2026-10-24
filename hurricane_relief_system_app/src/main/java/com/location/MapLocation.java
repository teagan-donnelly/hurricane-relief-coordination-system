package com.location;

/**
 * Represents one address after it has been converted
 * into latitude and longitude coordinates.
 */
public class MapLocation {

    private final Address address;
    private final Coordinates coordinates;

    /**
     * Constructor for a map location.
     * @param address Address of the location
     * @param coordinates Coordinates of the address
     */
    public MapLocation(Address address, Coordinates coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }

    /**
     * Gets the address.
     * @return Address of the location
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Gets the coordinates.
     * @return Coordinates of the location
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Returns the location as a string.
     * @return Address of the location as a string
     */
    @Override
    public String toString() {
        return address.toString();
    }
}