package com.gradems.service;

/**
 * Thin abstraction over the underlying AI chat framework.
 *
 * <p>Isolating the framework behind this seam keeps {@link ChatService} easy to unit-test
 * (the fluent Spring AI {@code ChatClient} API is awkward to mock directly) and confines any
 * future framework change to a single implementation.
 */
public interface AiChatClient {

    /**
     * Generates a reply for a message within a conversation.
     *
     * @param conversationId conversation key used to scope chat memory
     * @param message        the user's message
     * @return the assistant's reply
     */
    String reply(String conversationId, String message);
}
