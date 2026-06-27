package com.example.chatapp.user.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.user.dto.UserProfileResponse;
import com.example.chatapp.user.entity.User;
import com.example.chatapp.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

@Service
public class PlaceholderUserService implements UserService {

    private final UserRepository userRepository;

    public PlaceholderUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfileResponse getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal principal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing authenticated user.");
        }

        User user = userRepository.findById(principal.userId())
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(principal.email())
                        .orElseGet(() -> createPlaceholderUser(principal)));

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                true
        );
    }

    private User createPlaceholderUser(JwtPrincipal principal) {
        User user = new User();
        user.setId(principal.userId());
        user.setEmail(principal.email().trim().toLowerCase());
        user.setDisplayName(principal.email().split("@")[0]);
        return userRepository.save(user);
    }
}
