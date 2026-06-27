package com.example.chatapp.message.controller;

import com.example.chatapp.common.constants.ApplicationConstants;
import com.example.chatapp.common.response.ApiResponse;
import com.example.chatapp.message.dto.MessageResponse;
import com.example.chatapp.message.service.MessageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApplicationConstants.API_V1 + "/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations/{conversationId}")
    public ApiResponse<List<MessageResponse>> byConversation(
            @PathVariable UUID conversationId,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "Conversation messages loaded successfully.",
                messageService.getConversationMessages(conversationId, authentication)
        );
    }
}
