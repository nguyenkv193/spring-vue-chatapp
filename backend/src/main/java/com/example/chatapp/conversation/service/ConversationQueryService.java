package com.example.chatapp.conversation.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.conversation.dto.ConversationSummaryResponse;
import com.example.chatapp.conversation.dto.CreateConversationRequest;
import com.example.chatapp.conversation.entity.Conversation;
import com.example.chatapp.conversation.entity.ConversationMember;
import com.example.chatapp.conversation.entity.ConversationMemberRole;
import com.example.chatapp.conversation.entity.ConversationType;
import com.example.chatapp.conversation.repository.ConversationMemberRepository;
import com.example.chatapp.conversation.repository.ConversationRepository;
import com.example.chatapp.message.repository.MessageRepository;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ConversationQueryService implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ConversationQueryService(
            ConversationRepository conversationRepository,
            ConversationMemberRepository conversationMemberRepository,
            MessageRepository messageRepository,
            UserRepository userRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ConversationSummaryResponse> getCurrentUserConversations(Authentication authentication) {
        User currentUser = resolveCurrentUser(authentication);

        return conversationRepository.findAllByMemberUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConversationSummaryResponse createConversation(CreateConversationRequest request, Authentication authentication) {
        User currentUser = resolveCurrentUser(authentication);

        Conversation conversation = new Conversation();
        conversation.setTitle(request.title().trim());
        conversation.setType(request.type() != null ? request.type() : ConversationType.GROUP);
        conversation.setCreatedBy(currentUser);

        Conversation savedConversation = conversationRepository.save(conversation);

        ConversationMember ownerMembership = new ConversationMember();
        ownerMembership.setConversation(savedConversation);
        ownerMembership.setUser(currentUser);
        ownerMembership.setRole(ConversationMemberRole.OWNER);
        ownerMembership.setJoinedAt(Instant.now());
        conversationMemberRepository.save(ownerMembership);

        return toResponse(savedConversation);
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

    private ConversationSummaryResponse toResponse(Conversation conversation) {
        String lastMessagePreview = messageRepository.findFirstByConversation_IdOrderBySentAtDesc(conversation.getId())
                .map(message -> message.getContent().length() > 120
                        ? message.getContent().substring(0, 117) + "..."
                        : message.getContent())
                .orElse(null);

        return new ConversationSummaryResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getType(),
                conversation.getUpdatedAt(),
                lastMessagePreview
        );
    }
}
