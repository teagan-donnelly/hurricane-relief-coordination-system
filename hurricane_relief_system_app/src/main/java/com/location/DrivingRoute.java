package com.location;

import java.util.ArrayList;

/**
 * Represents a driving route between two locations.
 */
public class DrivingRoute {

    private final MapLocation startingLocation;
    private final MapLocation endingLocation;
    private final ArrayList<Coordinates> routePoints;

    /**
     * Constructor for a driving route.
     * @param startingLocation Where the route starts
     * @param endingLocation Where the route ends
     * @param routePoints List of points along the route
     */
    public DrivingRoute(
            MapLocation startingLocation,
            MapLocation endingLocation,
            ArrayList<Coordinates> routePoints
    ) {
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
        this.routePoints = routePoints;
    }

    /**
     * Gets the starting location.
     * @return Starting location of the route
     */
    public MapLocation getStartingLocation() {
        return startingLocation;
    }

    /**
     * Gets the ending location.
     * @return Ending location of the route
     */
    public MapLocation getEndingLocation() {
        return endingLocation;
    }

    /**
     * Gets the points along the route.
     * @return List of coordinates along the route
     */
    public ArrayList<Coordinates> getRoutePoints() {
        return routePoints;
    }
}