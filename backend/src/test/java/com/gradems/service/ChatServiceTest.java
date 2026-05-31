package com.gradems.service;

import com.gradems.config.AiProperties;
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

    private final Assistant assistant = mock(Assistant.class);

    private static AiProperties propsWithKey(String apiKey) {
        return new AiProperties("anthropic",
                new AiProperties.Anthropic(apiKey, "claude-haiku-4-5-20251001", 0.7, 1024));
    }

    @Test
    @DisplayName("chat - placeholder key returns stub response without calling the model")
    void chat_withPlaceholderKey_returnsStubAndSkipsModel() {
        // Arrange
        ChatService service = new ChatService(assistant, propsWithKey("placeholder-set-a-real-sk-ant-key"));

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", null));

        // Assert
        assertThat(response.reply()).contains("[AI not configured]");
        assertThat(response.conversationId()).isNotBlank();
        verifyNoInteractions(assistant);
    }

    @Test
    @DisplayName("chat - real key delegates to the assistant and returns its reply")
    void chat_withRealKey_callsAssistant() {
        // Arrange
        ChatService service = new ChatService(assistant, propsWithKey("sk-ant-test-key"));
        when(assistant.chat(eq("conv-1"), eq("hello"))).thenReturn("Hi there!");

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", "conv-1"));

        // Assert
        assertThat(response.reply()).isEqualTo("Hi there!");
        assertThat(response.conversationId()).isEqualTo("conv-1");
        verify(assistant, times(1)).chat("conv-1", "hello");
    }

    @Test
    @DisplayName("chat - generates a new conversationId when none is provided")
    void chat_withoutConversationId_generatesNewId() {
        // Arrange
        ChatService service = new ChatService(assistant, propsWithKey("sk-ant-test-key"));
        when(assistant.chat(anyString(), eq("hello"))).thenReturn("reply");

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", null));

        // Assert
        assertThat(response.conversationId()).isNotBlank();
        verify(assistant).chat(eq(response.conversationId()), eq("hello"));
    }

    @Test
    @DisplayName("chat - preserves a provided conversationId")
    void chat_withConversationId_preservesIt() {
        // Arrange
        ChatService service = new ChatService(assistant, propsWithKey("sk-ant-test-key"));
        when(assistant.chat(anyString(), anyString())).thenReturn("reply");

        // Act
        ChatResponse response = service.chat(new ChatRequest("hello", "existing-id"));

        // Assert
        assertThat(response.conversationId()).isEqualTo("existing-id");
    }
}
