package com.gradems.service;

import com.gradems.config.AiProperties;
import com.gradems.dto.request.ChatRequest;
import com.gradems.dto.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Orchestrates a chat turn: resolves/creates a conversation id and delegates to the LangChain4j
 * {@link Assistant}.
 *
 * <p>While the configured API key is still a placeholder, this returns a canned response instead
 * of calling the model, so the whole request/response flow can be tested without credentials.
 * As soon as a real {@code sk-ant-...} key is configured, the real model is used automatically.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final Assistant assistant;
    private final AiProperties properties;

    public ChatResponse chat(ChatRequest request) {
        String conversationId = StringUtils.hasText(request.conversationId())
                ? request.conversationId()
                : UUID.randomUUID().toString();

        if (!properties.hasRealApiKey()) {
            log.warn("AI chat invoked without a real API key; returning a stub response. "
                    + "Set ANTHROPIC_API_KEY to a real key to enable live responses.");
            return new ChatResponse(conversationId,
                    "[AI not configured] This is a placeholder response. Set a real ANTHROPIC_API_KEY "
                            + "(starting with \"sk-ant-\") and restart to get live answers. "
                            + "You said: \"" + request.message() + "\"");
        }

        String reply = assistant.chat(conversationId, request.message());
        return new ChatResponse(conversationId, reply);
    }
}
