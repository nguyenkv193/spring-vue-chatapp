package com.example.chatapp.message.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.message.dto.MessageResponse;
import com.example.chatapp.message.entity.Message;
import com.example.chatapp.message.repository.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageQueryService implements MessageService {

    private final MessageRepository messageRepository;

    public MessageQueryService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<MessageResponse> getConversationMessages(UUID conversationId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing authenticated user.");
        }

        return messageRepository.findTop50ByConversation_IdOrderBySentAtAsc(conversationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender() != null ? message.getSender().getId() : null,
                message.getSender() != null ? message.getSender().getDisplayName() : "System",
                message.getContent(),
                message.getMessageType(),
                message.getSentAt()
        );
    }
}
