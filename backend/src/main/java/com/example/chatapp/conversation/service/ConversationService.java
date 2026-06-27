package com.example.chatapp.conversation.service;

import com.example.chatapp.conversation.dto.ConversationSummaryResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ConversationService {

    List<ConversationSummaryResponse> getCurrentUserConversations(Authentication authentication);
}
