package com.example.chatapp.user.dto;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String displayName,
        boolean placeholder
) {
}
