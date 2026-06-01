package com.gradems.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the Spring AI building blocks: a windowed {@link ChatMemory} and a {@link ChatClient}
 * preconfigured with a default system prompt and a memory advisor.
 *
 * <p>The underlying Anthropic {@code ChatModel} and {@code ChatClient.Builder} are
 * auto-configured by {@code spring-ai-starter-model-anthropic} from the {@code spring.ai.anthropic.*}
 * properties. No network call happens at construction time, and {@code ChatService} only invokes
 * the model when a real API key is configured.
 */
@Configuration
public class AiConfig {

    private static final int MAX_MEMORY_MESSAGES = 20;

    private static final String SYSTEM_PROMPT = """
            You are a helpful assistant embedded in the Intelligent Student Information &
            Performance Tracking System. You help teachers, students, and administrators with
            questions about students, courses, and academic performance. Be concise and clear.
            If you do not yet have access to specific data, say so plainly.
            """;

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(MAX_MEMORY_MESSAGES)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
