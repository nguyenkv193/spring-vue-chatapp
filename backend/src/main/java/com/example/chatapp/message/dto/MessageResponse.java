package com.example.chatapp.message.dto;

import com.example.chatapp.message.entity.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String senderDisplayName,
        String content,
        MessageType messageType,
        Instant sentAt
) {
}
