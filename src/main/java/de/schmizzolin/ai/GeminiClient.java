package de.schmizzolin.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

public final class GeminiClient {
    private static final URI API_URL = URI.create("https://generativelanguage.googleapis.com/v1beta/interactions");
    private static final String MODEL = "gemini-3.8-flash";
    private static final ObjectMapper JSON = new ObjectMapper();

    private final String apiKey;
    private final HttpClient httpClient;

    public GeminiClient(String apiKey) {
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey");
        if (apiKey.isBlank()) {
            throw new IllegalArgumentException("Der API-Schlüssel darf nicht leer sein.");
        }
        this.httpClient = HttpClient.newHttpClient();
    }

    public String generateText(String prompt) throws IOException, InterruptedException {
        Objects.requireNonNull(prompt, "prompt");
        if (prompt.isBlank()) {
            throw new IllegalArgumentException("Der Prompt darf nicht leer sein.");
        }

        ObjectNode requestBody = JSON.createObjectNode();
        requestBody.put("model", MODEL);
        requestBody.put("input", prompt);

        HttpRequest request = HttpRequest.newBuilder(API_URL)
                .timeout(Duration.ofSeconds(60))
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Gemini API Fehler (" + response.statusCode() + "): " + response.body());
        }

        return readAnswer(JSON.readTree(response.body()));
    }

    private static String readAnswer(JsonNode responseJson) throws IOException {
        if (responseJson == null || !"completed".equals(responseJson.path("status").asText())) {
            throw new IOException("Die Gemini-Anfrage wurde nicht abgeschlossen: " + responseJson);
        }

        StringBuilder answer = new StringBuilder();
        for (JsonNode step : responseJson.path("steps")) {
            if (!"model_output".equals(step.path("type").asText())) {
                continue;
            }
            for (JsonNode content : step.path("content")) {
                if ("text".equals(content.path("type").asText()) && content.path("text").isTextual()) {
                    if (!answer.isEmpty()) {
                        answer.append(System.lineSeparator());
                    }
                    answer.append(content.path("text").asText());
                }
            }
        }

        if (answer.isEmpty()) {
            throw new IOException("Die API-Antwort enthält keinen Text.");
        }
        return answer.toString();
    }
}
