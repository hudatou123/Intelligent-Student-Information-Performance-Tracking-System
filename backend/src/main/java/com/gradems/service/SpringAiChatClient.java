package com.gradems.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * Spring AI implementation of {@link AiChatClient}. Delegates to the configured
 * {@link ChatClient}, scoping chat memory by conversation id via the memory advisor.
 */
@Component
public class SpringAiChatClient implements AiChatClient {

    private final ChatClient chatClient;

    public SpringAiChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String reply(String conversationId, String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
