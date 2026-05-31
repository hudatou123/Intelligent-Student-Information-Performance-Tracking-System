package com.gradems.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A chat message from the client.
 *
 * @param message        the user's message (required)
 * @param conversationId optional id to continue an existing conversation; when blank, a new
 *                       conversation is started and its id is returned in the response
 */
public record ChatRequest(
        @NotBlank(message = "Message is required") String message,
        String conversationId
) {}
