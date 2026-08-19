package com.pranit.chat.controller;

import com.pranit.chat.dto.QueryResponse;
import com.pranit.chat.entities.constant.Provider;
import com.pranit.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/helpdesk")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<QueryResponse> getResponse(
            @RequestBody String query,
            @RequestParam(required = false, defaultValue = "NVIDIA") Provider provider,
            @RequestHeader("conversationId") UUID conversationId) {
        final QueryResponse response = chatService.getResponseFromAssistant(provider, query, conversationId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
