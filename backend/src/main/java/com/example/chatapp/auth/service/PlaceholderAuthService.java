package com.example.chatapp.auth.service;

import com.example.chatapp.auth.dto.AuthTokenResponse;
import com.example.chatapp.auth.dto.LoginRequest;
import com.example.chatapp.auth.security.JwtService;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PlaceholderAuthService implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public PlaceholderAuthService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public AuthTokenResponse login(LoginRequest request) {
        if (!jwtService.isConfigured()) {
            throw new ApiException(
                    HttpStatus.PRECONDITION_FAILED,
                    "JWT placeholder login is disabled until APP_JWT_SECRET is configured."
            );
        }

        String normalizedEmail = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseGet(() -> createPlaceholderUser(normalizedEmail));

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());

        return new AuthTokenResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                accessToken,
                jwtService.getExpirationInstant(),
                true
        );
    }

    private User createPlaceholderUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setDisplayName(email.split("@")[0]);
        return userRepository.save(user);
    }
}
