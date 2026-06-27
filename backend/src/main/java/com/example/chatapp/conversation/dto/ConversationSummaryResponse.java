package com.example.chatapp.conversation.dto;

import com.example.chatapp.conversation.entity.ConversationType;

import java.time.Instant;
import java.util.UUID;

public record ConversationSummaryResponse(
        UUID id,
        String title,
        ConversationType type,
        Instant updatedAt,
        String lastMessagePreview
) {
}
