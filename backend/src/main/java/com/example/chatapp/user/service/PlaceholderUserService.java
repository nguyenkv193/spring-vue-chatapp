package com.example.chatapp.user.service;

import com.example.chatapp.auth.security.JwtPrincipal;
import com.example.chatapp.common.exception.ApiException;
import com.example.chatapp.user.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class PlaceholderUserService implements UserService {

    @Override
    public UserProfileResponse getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal principal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing authenticated user.");
        }

        return new UserProfileResponse(
                principal.userId(),
                principal.email(),
                principal.email().split("@")[0],
                true
        );
    }
}
