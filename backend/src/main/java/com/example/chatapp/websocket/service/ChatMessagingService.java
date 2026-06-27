package com.example.chatapp.websocket.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.constants.ApplicationConstants;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.conversation.entity.Conversation;
import com.example.chatapp.conversation.repository.ConversationMemberRepository;
import com.example.chatapp.conversation.repository.ConversationRepository;
import com.example.chatapp.message.entity.Message;
import com.example.chatapp.message.entity.MessageType;
import com.example.chatapp.message.repository.MessageRepository;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;
import com.example.chatapp.websocket.dto.ChatInboundMessage;
import com.example.chatapp.websocket.dto.ChatOutboundMessage;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;

@Service
public class ChatMessagingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatMessagingService(
            SimpMessagingTemplate messagingTemplate,
            ConversationRepository conversationRepository,
            ConversationMemberRepository conversationMemberRepository,
            MessageRepository messageRepository,
            UserRepository userRepository
    ) {
        this.messagingTemplate = messagingTemplate;
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChatOutboundMessage handleIncomingMessage(ChatInboundMessage inboundMessage, Principal principal) {
        User currentUser = resolveCurrentUser(principal);

        Conversation conversation = conversationRepository.findById(inboundMessage.conversationId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Conversation was not found."));

        if (!conversationMemberRepository.existsByConversation_IdAndUser_Id(conversation.getId(), currentUser.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have access to this conversation.");
        }

        Instant sentAt = Instant.now();

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(currentUser);
        message.setContent(inboundMessage.content().trim());
        message.setMessageType(inboundMessage.messageType() != null ? inboundMessage.messageType() : MessageType.TEXT);
        message.setSentAt(sentAt);

        Message savedMessage = messageRepository.save(message);

        conversation.setUpdatedAt(sentAt);
        conversationRepository.save(conversation);

        ChatOutboundMessage outboundMessage = new ChatOutboundMessage(
                savedMessage.getId(),
                conversation.getId(),
                currentUser.getId(),
                currentUser.getDisplayName(),
                savedMessage.getContent(),
                savedMessage.getMessageType(),
                savedMessage.getSentAt(),
                true
        );

        messagingTemplate.convertAndSend(
                ApplicationConstants.WEBSOCKET_TOPIC_PREFIX + "/conversations/" + inboundMessage.conversationId(),
                outboundMessage
        );

        return outboundMessage;
    }

    private User resolveCurrentUser(Principal principal) {
        JwtPrincipal jwtPrincipal = resolvePrincipal(principal);

        return userRepository.findById(jwtPrincipal.userId())
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(jwtPrincipal.email())
                        .orElseGet(() -> createPlaceholderUser(jwtPrincipal)));
    }

    private JwtPrincipal resolvePrincipal(Principal principal) {
        if (principal instanceof JwtPrincipal jwtPrincipal) {
            return jwtPrincipal;
        }

        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof JwtPrincipal jwtPrincipal) {
            return jwtPrincipal;
        }

        throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing authenticated user.");
    }

    private User createPlaceholderUser(JwtPrincipal principal) {
        User user = new User();
        user.setId(principal.userId());
        user.setEmail(principal.email().trim().toLowerCase());
        user.setDisplayName(principal.email().split("@")[0]);
        return userRepository.save(user);
    }
}