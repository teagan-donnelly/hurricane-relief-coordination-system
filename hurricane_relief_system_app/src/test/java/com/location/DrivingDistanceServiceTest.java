package com.location;

import static org.junit.Assert.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Flow;
import javax.net.ssl.SSLSession;
import org.junit.Test;

/**
 * Tests for {@link DrivingDistanceService}. The map api is replaced with fake
 * responses so the tests don't need an api key or an internet connection.
 * @author Jorge Naranjo Jr.
 *
 * 
 *
 * +---------------------------------------+------------------------------------------------------------------------------------------------------------------+
 * | Test                                  | Reasoning                                                                                                        |
 * +---------------------------------------+------------------------------------------------------------------------------------------------------------------+
 * | returnsDrivingDistanceFromMapService  | Shelters are picked by driving distance, so the service must return the distance the map service gives back.     |
 * | zeroDistanceIsAllowed                 | A family could already be at the shelter's address, so a distance of 0 should not be treated as an error.        |
 * | missingApiKeyThrowsException          | Without an API key the lookup can't work, so the service should fail clearly instead of guessing a distance.     |
 * | blankApiKeyThrowsException            | A key made of only spaces is the same as no key, so it should be rejected before contacting the map service.     |
 * | missingApiKeyDoesNotContactMapService | Checking the key first avoids sending a request that is guaranteed to fail and wasting time during an emergency. |
 * | failedLookupThrowsException           | If the map service is down or returns an error, the app should not pick a shelter using bad data.                |
 * | responseWithoutRouteThrowsException   | Flooded or closed roads can leave no route, and the service should report that instead of making up a distance.  |
 * | negativeDistanceThrowsException       | A negative distance is impossible, and accepting it could make a far away shelter look like the closest one.     |
 * | sendsLongitudeBeforeLatitude          | The map service expects longitude first, and mixing up the order would route families toward the wrong place.    |
 * | sendsApiKeyInAuthorizationHeader      | The map service only accepts the key in the Authorization header, so without it every lookup would fail.         |
 * +---------------------------------------+------------------------------------------------------------------------------------------------------------------+
 */
public class DrivingDistanceServiceTest {

    private HttpRequest sentRequest;

    /**
     * Makes a service that answers with a fake response instead of calling the map api.
     * @param apiKey Api key the service should use
     * @param status HTTP status code the fake map api returns
     * @param body Body the fake map api returns
     * @return Test service
     */
    private DrivingDistanceService service(String apiKey, int status, String body) {
        return new DrivingDistanceService() {
            protected String getApiKey() {
                return apiKey;
            }

            protected HttpResponse<String> send(HttpRequest request) {
                sentRequest = request;
                return response(status, body);
            }
        };
    }

    /**
     * Makes a map api response body with one route.
     * @param distance Length of the route in meters
     * @return Response body
     */
    private String route(double distance) {
        return "{\"routes\":[{\"summary\":{\"distance\":" + distance + "}}]}";
    }

    /**
     * Makes a fake map api response.
     * @param status HTTP status code
     * @param body Response body
     * @return Fake response
     */
    private HttpResponse<String> response(int status, String body) {
        return new HttpResponse<String>() {
            public int statusCode() { return status; }
            public HttpRequest request() { return sentRequest; }
            public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
            public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (name, value) -> true); }
            public String body() { return body; }
            public Optional<SSLSession> sslSession() { return Optional.empty(); }
            public URI uri() { return null; }
            public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
        };
    }

    /**
     * Reads the body that was sent to the map api.
     * @param request Request that was sent
     * @return Body of the request
     */
    private String bodyOf(HttpRequest request) {
        StringBuilder body = new StringBuilder();
        request.bodyPublisher().get().subscribe(new Flow.Subscriber<ByteBuffer>() {
            public void onSubscribe(Flow.Subscription subscription) { subscription.request(Long.MAX_VALUE); }
            public void onNext(ByteBuffer bytes) { body.append(StandardCharsets.UTF_8.decode(bytes)); }
            public void onError(Throwable error) { }
            public void onComplete() { }
        });
        return body.toString();
    }

    /**
     * Tests that the distance from the map api is returned.
     */
    @Test
    public void returnsDrivingDistanceFromMapService() throws Exception {
        double distance = service("test-key", 200, route(4523.7)).getDistance(32.7765, -79.9311, 32.79, -79.94);
        assertEquals(4523.7, distance, 0.001);
    }

    /**
     * Tests that a distance of zero is allowed when the start and end are the same place.
     */
    @Test
    public void zeroDistanceIsAllowed() throws Exception {
        double distance = service("test-key", 200, route(0)).getDistance(32.7765, -79.9311, 32.7765, -79.9311);
        assertEquals(0.0, distance, 0.001);
    }

    /**
     * Tests that a missing api key throws an exception.
     */
    @Test(expected = IOException.class)
    public void missingApiKeyThrowsException() throws Exception {
        service(null, 200, route(4523.7)).getDistance(32.7765, -79.9311, 32.79, -79.94);
    }

    /**
     * Tests that an api key of only spaces throws an exception.
     */
    @Test(expected = IOException.class)
    public void blankApiKeyThrowsException() throws Exception {
        service("   ", 200, route(4523.7)).getDistance(32.7765, -79.9311, 32.79, -79.94);
    }

    /**
     * Tests that the map api is never called when the api key is missing.
     */
    @Test
    public void missingApiKeyDoesNotContactMapService() throws Exception {
        try {
            service(null, 200, route(4523.7)).getDistance(32.7765, -79.9311, 32.79, -79.94);
        } catch (IOException e) {
            // expected
        }
        assertNull(sentRequest);
    }

    /**
     * Tests that an error from the map api throws an exception.
     */
    @Test(expected = IOException.class)
    public void failedLookupThrowsException() throws Exception {
        service("test-key", 500, "{\"error\":\"Server error\"}").getDistance(32.7765, -79.9311, 32.79, -79.94);
    }

    /**
     * Tests that a response with no route throws an exception.
     */
    @Test(expected = IOException.class)
    public void responseWithoutRouteThrowsException() throws Exception {
        service("test-key", 200, "{\"routes\":[]}").getDistance(32.7765, -79.9311, 32.79, -79.94);
    }

    /**
     * Tests that a negative distance from the map api throws an exception.
     */
    @Test(expected = IOException.class)
    public void negativeDistanceThrowsException() throws Exception {
        service("test-key", 200, route(-5)).getDistance(32.7765, -79.9311, 32.79, -79.94);
    }

    /**
     * Tests that the coordinates are sent as longitude then latitude, which is the order the map api expects.
     */
    @Test
    public void sendsLongitudeBeforeLatitude() throws Exception {
        service("test-key", 200, route(4523.7)).getDistance(32.7765, -79.9311, 32.79, -79.94);
        assertTrue(bodyOf(sentRequest).contains("[[-79.9311,32.7765],[-79.94,32.79]]"));
    }

    /**
     * Tests that the api key is sent in the Authorization header.
     */
    @Test
    public void sendsApiKeyInAuthorizationHeader() throws Exception {
        service("test-key", 200, route(4523.7)).getDistance(32.7765, -79.9311, 32.79, -79.94);
        assertEquals("test-key", sentRequest.headers().firstValue("Authorization").orElse(null));
    }
}
