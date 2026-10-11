package de.schmizzolin.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * @see <a href="https://docs.langchain4j.dev/integrations/language-models/google-ai-gemini/">LangChain4j Gemini</a>
 * @see <a href="https://ai.google.dev/api/generate-content">Google Gemini REST API</a>
 */
public final class GeminiClient {
    private static final String MODEL = "gemini-3.8-flash";

    private final ChatModel model;

    public GeminiClient(String apiKey) {
        Objects.requireNonNull(apiKey, "apiKey");
        if (apiKey.isBlank()) {
            throw new IllegalArgumentException("Der API-Schlüssel darf nicht leer sein.");
        }
        this.model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(MODEL)
                .timeout(Duration.ofSeconds(60))
                .build();
    }

    public String generateText(String prompt) throws IOException {
        Objects.requireNonNull(prompt, "prompt");
        if (prompt.isBlank()) {
            throw new IllegalArgumentException("Der Prompt darf nicht leer sein.");
        }

        String answer = model.chat(prompt);
        if (answer == null || answer.isBlank()) {
            throw new IOException("Die API-Antwort enthält keinen Text.");
        }
        return answer;
    }
}
