package com.example.chatapp.user.service;

import com.example.chatapp.user.dto.UserProfileResponse;
import org.springframework.security.core.Authentication;

public interface UserService {

    UserProfileResponse getCurrentUser(Authentication authentication);
}
