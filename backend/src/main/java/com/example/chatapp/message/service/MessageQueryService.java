package com.example.chatapp.message.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.conversation.repository.ConversationMemberRepository;
import com.example.chatapp.message.dto.MessageResponse;
import com.example.chatapp.message.entity.Message;
import com.example.chatapp.message.repository.MessageRepository;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MessageQueryService implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;

    public MessageQueryService(
            MessageRepository messageRepository,
            ConversationMemberRepository conversationMemberRepository,
            UserRepository userRepository
    ) {
        this.messageRepository = messageRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getConversationMessages(UUID conversationId, Authentication authentication) {
        User currentUser = resolveCurrentUser(authentication);

        if (!conversationMemberRepository.existsByConversation_IdAndUser_Id(conversationId, currentUser.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have access to this conversation.");
        }

        return messageRepository.findTop50ByConversation_IdOrderBySentAtAsc(conversationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User resolveCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal principal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing authenticated user.");
        }

        return userRepository.findById(principal.userId())
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(principal.email())
                        .orElseGet(() -> createPlaceholderUser(principal)));
    }

    private User createPlaceholderUser(JwtPrincipal principal) {
        User user = new User();
        user.setId(principal.userId());
        user.setEmail(principal.email().trim().toLowerCase());
        user.setDisplayName(principal.email().split("@")[0]);
        return userRepository.save(user);
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