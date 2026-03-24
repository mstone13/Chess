package communicator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientCommunicator {

    private final HttpClient client;
    private final String serverUrl;

    public ClientCommunicator(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
    }

    public String sendPostRequest(String endpoint, String jsonBody, String authToken) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + endpoint))
                    .header("Content-Type", "application/json");

            if (authToken != null && !authToken.isBlank()) {
                builder.header("Authorization", authToken);
            }

            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Request failed: " + e.getMessage(), e);
        }
    }

    public String sendGetRequest(String endpoint, String authToken) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + endpoint))
                    .header("Content-Type", "application/json");

            if (authToken != null && !authToken.isBlank()) {
                builder.header("Authorization", authToken);
            }
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Request failed: " + e.getMessage(), e);
        }
    }

    public String sendPutRequest(String endpoint, String jsonBody, String authToken) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + endpoint))
                    .header("Content-Type", "application/json");

            if (authToken != null && !authToken.isBlank()) {
                builder.header("Authorization", authToken);
            }

            HttpRequest request = builder
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Request failed: " + e.getMessage(), e);
        }
    }

    public void sendDeleteRequest(String endpoint) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + endpoint))
                    .header("Content-Type", "application/json");

            HttpRequest request = builder.DELETE().build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            response.body();

        } catch (Exception e) {
            throw new RuntimeException("Failed to clear application: " + e.getMessage());
        }
    }

}
