package ru.blogplatform.client.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HTTPUtility {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String domain = "http://localhost:8080";

    public static String sendGetRequest(String JWT, String URL) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(domain + URL))
                .header("Authorization", "Bearer " + JWT)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String sendPostRequest(String JWT, String URL, Map<String, String> body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(domain + URL))
                .header("Authorization", "Bearer " + JWT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String sendPutRequest(String JWT, String URL, Map<String, String> body) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(domain + URL))
                .header("Authorization", "Bearer " + JWT)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String sendDeleteRequest(String JWT, String URL) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(domain + URL))
                .header("Authorization", "Bearer " + JWT)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
