package com.example.chatapp.conversation.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.conversation.dto.ConversationSummaryResponse;
import com.example.chatapp.conversation.entity.Conversation;
import com.example.chatapp.conversation.repository.ConversationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationQueryService implements ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationQueryService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Override
    public List<ConversationSummaryResponse> getCurrentUserConversations(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal principal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing authenticated user.");
        }

        return conversationRepository.findAllByMemberUserId(principal.userId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ConversationSummaryResponse toResponse(Conversation conversation) {
        return new ConversationSummaryResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getType(),
                conversation.getUpdatedAt(),
                null
        );
    }
}
