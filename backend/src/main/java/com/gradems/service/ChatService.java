package com.gradems.service;

import com.gradems.dto.request.ChatRequest;
import com.gradems.dto.response.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Orchestrates a chat turn: resolves/creates a conversation id and delegates to {@link AiChatClient}.
 *
 * <p>While no real API key is configured, this returns a canned response instead of calling the
 * model, so the whole request/response flow can be tested without credentials. As soon as a real
 * Google API key is set, the real model is used automatically — no code change required.
 */
@Service
@Slf4j
public class ChatService {

    private final AiChatClient aiChatClient;
    private final boolean aiEnabled;

    public ChatService(AiChatClient aiChatClient,
                       @Value("${spring.ai.openai.api-key:}") String apiKey) {
        this.aiChatClient = aiChatClient;
        this.aiEnabled = apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("placeholder");
    }

    public ChatResponse chat(ChatRequest request) {
        String conversationId = StringUtils.hasText(request.conversationId())
                ? request.conversationId()
                : UUID.randomUUID().toString();

        if (!aiEnabled) {
            log.warn("AI chat invoked without a real API key; returning a stub response. "
                    + "Set GOOGLE_API_KEY to a real key to enable live responses.");
            return new ChatResponse(conversationId,
                    "[AI not configured] This is a placeholder response. Set a real GOOGLE_API_KEY "
                            + "and restart to get live answers. "
                            + "You said: \"" + request.message() + "\"");
        }

        String reply = aiChatClient.reply(conversationId, request.message());
        return new ChatResponse(conversationId, reply);
    }
}
