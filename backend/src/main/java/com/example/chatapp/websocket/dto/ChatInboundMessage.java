package com.example.chatapp.websocket.dto;

import com.example.chatapp.message.entity.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChatInboundMessage(
        @NotNull(message = "Conversation id is required")
        UUID conversationId,

        @NotBlank(message = "Content is required")
        @Size(max = 4000, message = "Content must be at most 4000 characters")
        String content,

        MessageType messageType
) {
}
