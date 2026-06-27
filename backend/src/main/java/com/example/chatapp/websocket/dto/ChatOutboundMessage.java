package com.example.chatapp.websocket.dto;

import com.example.chatapp.message.entity.MessageType;

import java.time.Instant;
import java.util.UUID;

public record ChatOutboundMessage(
        UUID messageId,
        UUID conversationId,
        UUID senderId,
        String senderDisplayName,
        String content,
        MessageType messageType,
        Instant sentAt,
        boolean persisted
) {
}
