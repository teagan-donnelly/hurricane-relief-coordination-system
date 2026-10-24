package com.model;

/**
 * @author Teagan Donnelly
 * Represents a location with coordinates and an address.
 */
public class Location {
    private double latitude;
    private double longitude;
    private String streetAddress;
    private int appartmentNum;
    private String city;
    private String state;
    private String zipcode;

    /**
     * Constructor for a location.
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param streetAddress Street address
     * @param appartmentNum Apartment number, 0 if there isn't one
     * @param city City
     * @param state State
     * @param zipcode Zip code
     */
    public Location(double latitude, double longitude, String streetAddress, int appartmentNum, String city, String state, String zipcode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
        this.appartmentNum = appartmentNum;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    /**
     * Changes the coordinates of the location.
     * @param latitude New latitude
     * @param longitude New longitude
     */
    public void changeCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Changes the address of the location.
     * @param streetAddress New street address
     * @param appartmentNum New apartment number
     * @param city New city
     * @param state New state
     * @param zipcode New zip code
     */
    public void changeAddress(String streetAddress, int appartmentNum,
                              String city, String state, String zipcode) {
        this.streetAddress = streetAddress;
        this.appartmentNum = appartmentNum;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    /**
     * Calculates the straight line distance to another location using the coordinates.
     * @param targetLocation Location to find the distance to
     * @return Distance between the two locations
     */
    public double calculateDistance(Location targetLocation) {
        double latitudeDifference = targetLocation.latitude - this.latitude;
        double longitudeDifference = targetLocation.longitude - this.longitude;

        return Math.sqrt(
            Math.pow(latitudeDifference, 2) +
            Math.pow(longitudeDifference, 2)
        );
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
     * Gets the street address.
     * @return Street address
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Gets the apartment number.
     * @return Apartment number
     */
    public int getApartmentNum() {
        return appartmentNum;
    }

    /**
     * Gets the city.
     * @return City
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the state.
     * @return State
     */
    public String getState() {
        return state;
    }

    /**
     * Gets the zip code.
     * @return Zip code
     */
    public String getZipcode() {
        return zipcode;
    }
}
