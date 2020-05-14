package Utils;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class Util {


    private static String getRequest(String endpoint) {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieve ngrok redirect url
     *
     * @param endpoint the ngrok server url
     */
    public static String getRedirectUrl(String endpoint) {
        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(getRequest(endpoint)));
        return (String) jsonObject.getJSONArray("tunnels").getJSONObject(0).get("public_url");
    }

}
