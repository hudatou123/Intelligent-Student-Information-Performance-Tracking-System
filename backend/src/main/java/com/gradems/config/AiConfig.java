package com.gradems.config;

import com.gradems.service.Assistant;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the LangChain4j building blocks: the Anthropic chat model, a per-conversation memory
 * provider, and the {@link Assistant} AI service.
 *
 * <p>The model bean is constructed even with a placeholder API key — no network call happens
 * until the assistant is actually invoked, and {@code ChatService} short-circuits placeholder
 * keys, so the application starts cleanly without real credentials.
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
@RequiredArgsConstructor
public class AiConfig {

    private static final int MAX_MEMORY_MESSAGES = 20;

    /**
     * Non-blank dummy key used only so the model can be constructed when no real key is set.
     * The builder rejects a blank key, but never makes a network call at construction time —
     * and {@code ChatService} short-circuits before invoking the model in placeholder mode.
     */
    private static final String DUMMY_API_KEY = "sk-ant-not-configured-placeholder";

    private final AiProperties properties;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        AiProperties.Anthropic anthropic = properties.anthropic();
        String apiKey = properties.hasRealApiKey() ? anthropic.apiKey() : DUMMY_API_KEY;
        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(anthropic.modelName())
                .temperature(anthropic.temperature())
                .maxTokens(anthropic.maxTokens())
                .build();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.withMaxMessages(MAX_MEMORY_MESSAGES);
    }

    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel, ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}
