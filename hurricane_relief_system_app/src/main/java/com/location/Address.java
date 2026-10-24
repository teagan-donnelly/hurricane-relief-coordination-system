package com.location;


/**
 * Represents a street address.
 */
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;

    /**
     * Constructor for an address.
     * @param street Street address
     * @param city City
     * @param state State
     * @param zip Zip code
     */
    public Address(String street, String city, String state, String zip){
        this.street = street;
        this. city = city;
        this.state = state;
        this.zip = zip;
    }

    /**
     * Gets the street address.
     * @return Street address
     */
    public String getStreet() {
        return street;
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
    public String getZip() {
        return zip;
    }

    /**
     * Looks up the coordinates of this address using the map api.
     * @return Coordinates of the address, or null if the lookup fails
     */
    public Coordinates getCoordinates() {
        try {
            return MapTools.getInstance().getCoordinates(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the address as a string.
     * @return Address in the format street, city, state zip
     */
    public String toString() {
        return String.format("%s, %s, %s %s", street, city, state, zip);
    }
}
