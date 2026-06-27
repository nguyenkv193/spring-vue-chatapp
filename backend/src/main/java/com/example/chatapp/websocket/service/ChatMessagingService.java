package com.example.chatapp.websocket.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.constants.ApplicationConstants;
import com.example.chatapp.message.entity.MessageType;
import com.example.chatapp.websocket.dto.ChatInboundMessage;
import com.example.chatapp.websocket.dto.ChatOutboundMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

@Service
public class ChatMessagingService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public ChatOutboundMessage handleIncomingMessage(ChatInboundMessage inboundMessage, Principal principal) {
        JwtPrincipal jwtPrincipal = principal instanceof JwtPrincipal typedPrincipal ? typedPrincipal : null;
        ChatOutboundMessage outboundMessage = new ChatOutboundMessage(
                UUID.randomUUID(),
                inboundMessage.conversationId(),
                jwtPrincipal != null ? jwtPrincipal.userId() : null,
                jwtPrincipal != null ? jwtPrincipal.email().split("@")[0] : "guest",
                inboundMessage.content(),
                inboundMessage.messageType() != null ? inboundMessage.messageType() : MessageType.TEXT,
                Instant.now(),
                false
        );

        messagingTemplate.convertAndSend(
                ApplicationConstants.WEBSOCKET_TOPIC_PREFIX + "/conversations/" + inboundMessage.conversationId(),
                outboundMessage
        );

        return outboundMessage;
    }
}
