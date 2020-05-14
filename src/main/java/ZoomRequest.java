import Utils.RateLimiterSingleton;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Simple wrapper for REST API request
 */
public class ZoomRequest {

    private HttpClient client;
    private final Zoom root;
    private final RateLimiterSingleton rateLimiter;

    private static final String CONTENT_TYPE_NAME = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * Setup a new API Client.
     *
     */
    public ZoomRequest(Zoom root) {
        this.root = root;
        this.client = HttpClient.newBuilder().build();
        this.rateLimiter = root.getRateLimitHandler();
    }

    /**
     * Get the URL for the given endpoint
     *
     * @param endpoint The endpoint
     * @return The full URL for the endpoint
     */
    private String getApiUrl(String endpoint) {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return root.getApiUrl() + endpoint;
    }

    /**
     * Helper function for GET requests
     */
    public HttpResponse<String> getRequest(String endpoint, int timeout, String[] headers, JSONObject requestObj) {
        try{
            URIBuilder uriBuilder = new URIBuilder(getApiUrl(endpoint));
            for(String key : requestObj.keySet()) {
                uriBuilder.addParameter(key, requestObj.getString(key));
            }
            rateLimiter.checkValid();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uriBuilder.build())
                    .headers(headers)
                    .GET()
                    .timeout(Duration.ofSeconds(timeout))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper function for POST requests
     * @param requestObj JSONObject with request parameters, works as "body" in request.
     */
    public HttpResponse<String> postRequest(String endpoint, int timeout, String[] headers, JSONObject requestObj) {
        try {
            rateLimiter.checkValid();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl(endpoint)))
                    .headers(headers)
                    .timeout(Duration.ofSeconds(timeout))
                    .POST(HttpRequest.BodyPublishers.ofString(requestObj.toString()))
                    .setHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper function for PATCH requests
     * @param requestObj JSONObject with request parameters, works as "body" in request.
     */
    public HttpResponse<String> patchRequest(String endpoint, int timeout, String[] headers, JSONObject requestObj) {
        try {
            rateLimiter.checkValid();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl(endpoint)))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(requestObj.toString()))
                    .headers(headers)
                    .timeout(Duration.ofSeconds(timeout))
                    .setHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper function for DELETE requests
     * @param requestObj JSONObject with request parameters, works as "body" in request.
     */
    public HttpResponse<String> deleteRequest(String endpoint, int timeout, String[] headers, JSONObject requestObj) {
        try {
            rateLimiter.checkValid();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl(endpoint)))
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(requestObj.toString()))
                    .headers(headers)
                    .timeout(Duration.ofSeconds(timeout))
                    .setHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper function for PUT requests
     * @param requestObj JSONObject with request parameters, works as "body" in request.
     */
    public HttpResponse<String> putRequest(String endpoint, int timeout, String[] headers, JSONObject requestObj) {
        try {
            rateLimiter.checkValid();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl(endpoint)))
                    .method("PUT", HttpRequest.BodyPublishers.ofString(requestObj.toString()))
                    .headers(headers)
                    .timeout(Duration.ofSeconds(timeout))
                    .setHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
