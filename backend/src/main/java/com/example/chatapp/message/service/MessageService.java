package com.example.chatapp.message.service;

import com.example.chatapp.message.dto.MessageResponse;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    List<MessageResponse> getConversationMessages(UUID conversationId, Authentication authentication);
}
