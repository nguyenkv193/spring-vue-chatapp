package com.example.chatapp.auth.security;

import java.security.Principal;
import java.util.UUID;

public record JwtPrincipal(
        UUID userId,
        String email
) implements Principal {
    @Override
    public String getName() {
        return email;
    }
}