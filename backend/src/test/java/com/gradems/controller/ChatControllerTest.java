package com.gradems.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradems.config.SecurityConfig;
import com.gradems.dto.request.ChatRequest;
import com.gradems.dto.response.ChatResponse;
import com.gradems.exception.GlobalExceptionHandler;
import com.gradems.security.JwtAuthenticationFilter;
import com.gradems.security.JwtTokenProvider;
import com.gradems.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class})
@DisplayName("ChatController Integration Tests")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /api/chat - authenticated request returns 200 with reply")
    @WithMockUser
    void chat_authenticated_returns200() throws Exception {
        ChatRequest request = new ChatRequest("Hello", null);
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(new ChatResponse("conv-1", "Hi there!"));

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.data.reply").value("Hi there!"));
    }

    @Test
    @DisplayName("POST /api/chat - blank message returns 400")
    @WithMockUser
    void chat_withBlankMessage_returns400() throws Exception {
        String invalidBody = "{\"message\": \"\"}";

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/chat - unauthenticated request is forbidden")
    void chat_withoutAuth_returns403() throws Exception {
        ChatRequest request = new ChatRequest("Hello", null);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
