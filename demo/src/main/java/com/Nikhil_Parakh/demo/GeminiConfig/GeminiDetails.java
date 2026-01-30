package com.Nikhil_Parakh.demo.GeminiConfig;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiDetails {

    private static final Logger log = LoggerFactory.getLogger(GeminiDetails.class);

    private static final String GEMINI_MODEL = "gemini-2.5-flash-lite";

    @Bean
    public ChatLanguageModel chatLanguageModel() {

        String apiKey = System.getenv("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "GEMINI_API_KEY environment variable is not set"
            );
        }

        log.info("Initializing Gemini ChatLanguageModel with model: {}", GEMINI_MODEL);

        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(GEMINI_MODEL)
                .build();
    }
}
