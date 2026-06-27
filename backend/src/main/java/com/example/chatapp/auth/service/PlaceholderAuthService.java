package com.example.chatapp.auth.service;

import com.example.chatapp.auth.dto.AuthTokenResponse;
import com.example.chatapp.auth.dto.LoginRequest;
import com.example.chatapp.auth.security.JwtService;
import com.example.chatapp.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlaceholderAuthService implements AuthService {

    private final JwtService jwtService;

    public PlaceholderAuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public AuthTokenResponse login(LoginRequest request) {
        if (!jwtService.isConfigured()) {
            throw new ApiException(
                    HttpStatus.PRECONDITION_FAILED,
                    "JWT placeholder login is disabled until APP_JWT_SECRET is configured."
            );
        }

        UUID userId = UUID.randomUUID();
        String accessToken = jwtService.generateAccessToken(userId, request.email());
        String displayName = request.email().split("@")[0];

        return new AuthTokenResponse(
                userId,
                request.email(),
                displayName,
                accessToken,
                jwtService.getExpirationInstant(),
                true
        );
    }
}
