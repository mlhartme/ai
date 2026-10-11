package de.schmizzolin.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public final class ChatGptExample {
    private static final URI API_URL = URI.create("https://api.openai.com/v1/responses");
    private static final String MODEL = "gpt-4.1-mini";
    private static final ObjectMapper JSON = new ObjectMapper();

    private ChatGptExample() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path apiKeyFile = Path.of(System.getProperty("user.home"), ".openaiApiKey");
        String apiKey = Files.readString(apiKeyFile).trim();
        if (apiKey.isBlank()) {
            throw new IllegalStateException("Die Datei " + apiKeyFile + " enthält keinen API-Schlüssel.");
        }

        ObjectNode requestBody = JSON.createObjectNode();
        requestBody.put("model", MODEL);
        requestBody.put("input", "Hello, wie geht es dir");

        HttpRequest request = HttpRequest.newBuilder(API_URL)
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("OpenAI API Fehler (" + response.statusCode() + "): " + response.body());
        }

        JsonNode responseJson = JSON.readTree(response.body());
        StringBuilder answer = new StringBuilder();
        for (JsonNode output : responseJson.path("output")) {
            for (JsonNode content : output.path("content")) {
                if ("output_text".equals(content.path("type").asText())) {
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
        System.out.println(answer);
    }
}
