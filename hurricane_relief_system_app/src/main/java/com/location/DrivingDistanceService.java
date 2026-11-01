package com.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Uses the map api to find the driving distance between two points.
 */
public class DrivingDistanceService {

    /**
     * Gets the driving distance between two points.
     * @param startLatitude Latitude of the starting point
     * @param startLongitude Longitude of the starting point
     * @param endLatitude Latitude of the ending point
     * @param endLongitude Longitude of the ending point
     * @return Driving distance in meters
     * @throws IOException if the api key is missing or the lookup fails
     * @throws InterruptedException if the request is interrupted
     */
    public double getDistance(double startLatitude, double startLongitude,
                              double endLatitude, double endLongitude)
            throws IOException, InterruptedException {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IOException("MAP_API_KEY must be set to compare driving distances.");
        }

        String body = "{\"coordinates\":[[" + startLongitude + "," + startLatitude +
            "],[" + endLongitude + "," + endLatitude + "]],\"units\":\"m\"}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.heigit.org/openrouteservice/v2/directions/driving-car"))
            .header("Authorization", apiKey)
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(20))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = send(request);
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

    /**
     * Gets the map api key.
     * @return The api key, or null if it is not set
     */
    protected String getApiKey() {
        return System.getenv("MAP_API_KEY");
    }

    /**
     * Sends a request to the map api.
     * @param request Request to send
     * @return Response from the map api
     * @throws IOException if the request fails
     * @throws InterruptedException if the request is interrupted
     */
    protected HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
