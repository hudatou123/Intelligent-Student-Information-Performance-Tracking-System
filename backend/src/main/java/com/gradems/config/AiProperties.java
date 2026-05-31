package com.gradems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the AI / LLM integration.
 *
 * <p>Bound from the {@code ai.*} section of application.yml. The API key is read from the
 * {@code ANTHROPIC_API_KEY} environment variable; until a real key (starting with
 * {@code sk-ant-}) is provided, the chat endpoint returns a placeholder response so the
 * feature can be exercised end-to-end without external credentials.
 */
@ConfigurationProperties(prefix = "ai")
public record AiProperties(
        String provider,
        Anthropic anthropic
) {
    public record Anthropic(
            String apiKey,
            String modelName,
            double temperature,
            int maxTokens
    ) {}

    /** True only when a real Anthropic key (starting with {@code sk-ant-}) is configured. */
    public boolean hasRealApiKey() {
        return anthropic != null
                && anthropic.apiKey() != null
                && anthropic.apiKey().startsWith("sk-ant-");
    }
}
