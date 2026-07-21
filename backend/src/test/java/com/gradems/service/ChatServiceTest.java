package com.gradems.service;

import com.gradems.dto.request.ChatRequest;
import com.gradems.dto.response.ChatResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("ChatService Unit Tests")
class ChatServiceTest {

    private final AiChatClient aiChatClient = mock(AiChatClient.class);

    private ChatService serviceWithKey(String apiKey) {
        return new ChatService(aiChatClient, apiKey);
    }

    @Test
    @DisplayName("chat - placeholder key returns stub response without calling the model")
    void chat_withPlaceholderKey_returnsStubAndSkipsModel() {
        // Arrange
        ChatService service = serviceWithKey("placeholder-set-a-real-google-api-key");

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", null));

        // Assert
        assertThat(response.reply()).contains("[AI not configured]");
        assertThat(response.conversationId()).isNotBlank();
        verifyNoInteractions(aiChatClient);
    }

    @Test
    @DisplayName("chat - real key delegates to the AI client and returns its reply")
    void chat_withRealKey_callsAiClient() {
        // Arrange
        ChatService service = serviceWithKey("AIza-test-key");
        when(aiChatClient.reply(eq("conv-1"), eq("hello"))).thenReturn("Hi there!");

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", "conv-1"));

        // Assert
        assertThat(response.reply()).isEqualTo("Hi there!");
        assertThat(response.conversationId()).isEqualTo("conv-1");
        verify(aiChatClient, times(1)).reply("conv-1", "hello");
    }

    @Test
    @DisplayName("chat - generates a new conversationId when none is provided")
    void chat_withoutConversationId_generatesNewId() {
        // Arrange
        ChatService service = serviceWithKey("AIza-test-key");
        when(aiChatClient.reply(anyString(), eq("hello"))).thenReturn("reply");

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", null));

        // Assert
        assertThat(response.conversationId()).isNotBlank();
        verify(aiChatClient).reply(eq(response.conversationId()), eq("hello"));
    }

    @Test
    @DisplayName("chat - preserves a provided conversationId")
    void chat_withConversationId_preservesIt() {
        // Arrange
        ChatService service = serviceWithKey("AIza-test-key");
        when(aiChatClient.reply(anyString(), anyString())).thenReturn("reply");

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", "existing-id"));

        // Assert
        assertThat(response.conversationId()).isEqualTo("existing-id");
    }
}
