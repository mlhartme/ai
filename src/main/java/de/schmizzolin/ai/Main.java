package de.schmizzolin.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path apiKeyFile = Path.of(System.getProperty("user.home"), ".geminiApiKey");
        String apiKey = Files.readString(apiKeyFile).trim();
        if (apiKey.isBlank()) {
            throw new IllegalStateException("missing api key: " + apiKeyFile);
        }
        GeminiClient client = new GeminiClient(apiKey);
        System.out.println(client.generateText("Hello, wie geht es dir"));
    }
}
