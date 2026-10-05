package com.model;

/**
 * @author Teagan Donnelly
 */
public class Location {
    private double latitude;
    private double longitude;
    private String streetAddress;
    private int appartmentNum;
    private String city;
    private String state;
    private String zipcode;

    public Location(double latitude, double longitude, String streetAddress, int appartmentNum, String city, String state, String zipcode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
        this.appartmentNum = appartmentNum;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public void changeCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void changeAddress(String streetAddress, int appartmentNum,
                              String city, String state, String zipcode) {
        this.streetAddress = streetAddress;
        this.appartmentNum = appartmentNum;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public double calculateDistance(Location targetLocation) {
        double latitudeDifference = targetLocation.latitude - this.latitude;
        double longitudeDifference = targetLocation.longitude - this.longitude;

        return Math.sqrt(
            Math.pow(latitudeDifference, 2) +
            Math.pow(longitudeDifference, 2)
        );
    }
}
