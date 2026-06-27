package com.example.chatapp.conversation.controller;

import com.example.chatapp.common.constants.ApplicationConstants;
import com.example.chatapp.common.response.ApiResponse;
import com.example.chatapp.conversation.dto.ConversationSummaryResponse;
import com.example.chatapp.conversation.service.ConversationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApplicationConstants.API_V1 + "/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ApiResponse<List<ConversationSummaryResponse>> list(Authentication authentication) {
        return ApiResponse.success(
                "Conversations loaded successfully.",
                conversationService.getCurrentUserConversations(authentication)
        );
    }
}
