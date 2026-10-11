package de.schmizzolin.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * @see <a href="https://docs.langchain4j.dev/integrations/language-models/google-ai-gemini/">LangChain4j Gemini</a>
 */
public final class Main {
    private static final String MODEL = "gemini-3.8-flash";

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        Path apiKeyFile = Path.of(System.getProperty("user.home"), ".geminiApiKey");
        String apiKey = Files.readString(apiKeyFile).trim();
        if (apiKey.isBlank()) {
            throw new IllegalStateException("missing api key: " + apiKeyFile);
        }
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(MODEL)
                .timeout(Duration.ofSeconds(60))
                .build();

        String answer = model.chat("Hello, wie geht es dir");
        if (answer == null || answer.isBlank()) {
            throw new IOException("Die API-Antwort enthält keinen Text.");
        }
        System.out.println(answer);
    }
}
