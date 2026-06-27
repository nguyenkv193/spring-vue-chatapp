package com.example.chatapp.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthTokenResponse(
        UUID userId,
        String email,
        String displayName,
        String accessToken,
        Instant expiresAt,
        boolean placeholder
) {
}
