package com.example.chatapp.conversation.dto;

import com.example.chatapp.conversation.entity.ConversationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
        @NotBlank(message = "Conversation title is required")
        @Size(max = 160, message = "Conversation title must be at most 160 characters")
        String title,

        ConversationType type
) {
}
