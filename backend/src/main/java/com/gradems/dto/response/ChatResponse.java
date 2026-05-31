package com.gradems.dto.response;

/**
 * The assistant's reply.
 *
 * @param conversationId id of the conversation (echo the same value in the next request to keep context)
 * @param reply          the assistant's generated message
 */
public record ChatResponse(
        String conversationId,
        String reply
) {}
