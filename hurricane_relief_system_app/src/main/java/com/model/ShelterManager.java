package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShelterManager{
    private ArrayList<Shelter> shelters;

    public ShelterManager(){
        this.shelters = DataLoader.getShelters();
    }

    public ShelterManager(ArrayList<Shelter> shelters){
        this.shelters = shelters;
    }

    public List<Shelter> sortShelters(Location target){
    List<Shelter> sortedShelters = new ArrayList<>(shelters);
    sortedShelters.sort(Comparator.comparingDouble(shelter -> shelter.getLocation().calculateDistance(target)));
    return sortedShelters;
    }

    public Shelter findShelter(UUID id){
        for(Shelter shelter : shelters){
            if(shelter.getId().equals(id)){
                return shelter;
            }
        }
        return null;
    }

    public static void registerShelter(String name, UUID id, Location location, int currentOccupancy, int capacity,
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status,
        boolean hasMedicalStaff, UUID admin) {

        ShelterManager manager = new ShelterManager();
        ArrayList<Shelter> shelters = manager.getShelters();

        shelters.add(new Shelter(name, id, location, currentOccupancy, capacity, 
                    petFriendly, accessibilityInfo, hasPower, status, hasMedicalStaff, admin));

    }

    public static void viewShelter(UUID id) {
        ShelterManager manager = new ShelterManager();
        ArrayList<Shelter> shelters = manager.getShelters();

        for (Shelter shelter : shelters) {
            if (shelter.getId().equals(id)) {
                String report = "Shelter name" + shelter.getName() + "\n" +
                    "Shelter location" + shelter.getLocation() + "\n" +
                    "Shelter capacity" + shelter.getCapacity() + "\n" +
                    "Shelter occupancy" + shelter.getCurrentOccupancy() + "\n" +
                    "Shelter space" + shelter.getAvailableSpace() + "\n" +
                    "Shelter occupants: " + shelter.getOccupants() + "\n" +
                    "Shelter pets: " + shelter.getPets();
                System.out.println(report);
            }
        }

    }

    public static void editShelter(String name, Location location, int currentOccupancy, int capacity, 
        boolean petFriendly, ArrayList<String> accessibilityInfo, boolean hasPower, ShelterStatus status,
        boolean hasMedicalStaff, UUID admin) {

        ShelterManager manager = new ShelterManager();
        ArrayList<Shelter> shelters = manager.getShelters();

    }

    /**
     * Explains why a shelter can't take a request.
     * @param shelter the shelter to check
     * @param request the request that needs a shelter
     * @param needsMedical true if someone in the household needs medical staff
     * @return the reason it doesn't work, or null if the shelter is suitable
     */
    public String getProblem(Shelter shelter, PersonalRequest request, boolean needsMedical) {
        if (shelter.getStatus() != ShelterStatus.OPEN) {
            return "not open (" + shelter.getStatus() + ")";
        }
        if (request.getPets() > 0 && !shelter.isPetFriendly()) {
            return "does not accept pets";
        }
        if (needsMedical && !shelter.hasMedicalStaff()) {
            return "no medical staff";
        }
        if (shelter.getAvailablePetSpace() < request.getPets()) {
            return "not enough pet spaces";
        }
        if (shelter.getAvailableSpace() < request.getHouseholdSize()) {
            int space = shelter.getAvailableSpace();
            return "only " + space + (space == 1 ? " space" : " spaces") + " left";
        }
        return null;
    }

    /**
     * Finds the suitable shelter closest to a request by driving distance.
     * @param request the request that needs a shelter
     * @param needsMedical true if someone in the household needs medical staff
     * @return the closest suitable shelter, or null if none are suitable
     */
    public Shelter findClosestSuitableShelter(PersonalRequest request, boolean needsMedical)
            throws IOException, InterruptedException {
        Shelter closest = null;
        double shortestDistance = Double.MAX_VALUE;
        for (Shelter shelter : shelters) {
            if (getProblem(shelter, request, needsMedical) == null) {
                double distance = getDrivingDistance(request.getLocation(), shelter.getLocation());
                System.out.printf("  %s: %.2f km by road%n", shelter.getName(), distance / 1000);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closest = shelter;
                }
            }
        }
        return closest;
    }

    private double getDrivingDistance(Location start, Location end)
            throws IOException, InterruptedException {
        String apiKey = System.getenv("MAP_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IOException("MAP_API_KEY must be set to compare driving distances.");
        }

        String body = "{\"coordinates\":[[" + start.getLongitude() + "," + start.getLatitude() +
            "],[" + end.getLongitude() + "," + end.getLatitude() + "]],\"units\":\"m\"}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.heigit.org/openrouteservice/v2/directions/driving-car"))
            .header("Authorization", apiKey)
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(20))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Driving distance lookup failed (HTTP " + response.statusCode() + ").");
        }

        JsonNode distance = new ObjectMapper().readTree(response.body())
            .path("routes").path(0).path("summary").path("distance");
        if (!distance.isNumber() || !Double.isFinite(distance.asDouble()) || distance.asDouble() < 0) {
            throw new IOException("Map service did not return a valid driving distance.");
        }
        return distance.asDouble();
    }

    public ArrayList<Shelter> getShelters() {
        return shelters;
    }
}
