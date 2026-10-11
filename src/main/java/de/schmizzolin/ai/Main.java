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
                .modelName("gemini-3.8-flash")
                .timeout(Duration.ofSeconds(60))
                .build();

        String answer = model.chat("Hello, wie geht es dir");
        System.out.println(answer);
    }
}
