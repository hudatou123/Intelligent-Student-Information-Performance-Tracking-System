package com.gradems.controller;

import com.gradems.dto.request.ChatRequest;
import com.gradems.dto.response.ApiResponse;
import com.gradems.dto.response.ChatResponse;
import com.gradems.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "Conversational AI assistant with per-conversation memory")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Send a message to the AI assistant",
            description = "Sends a message and returns the assistant's reply. "
                    + "Pass the returned conversationId on subsequent requests to keep context.")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(ApiResponse.success(chatService.chat(request)));
    }
}
