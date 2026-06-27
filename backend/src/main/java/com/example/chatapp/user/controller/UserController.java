package com.example.chatapp.user.controller;

import com.example.chatapp.common.constants.ApplicationConstants;
import com.example.chatapp.common.response.ApiResponse;
import com.example.chatapp.user.dto.UserProfileResponse;
import com.example.chatapp.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApplicationConstants.API_V1 + "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> currentUser(Authentication authentication) {
        return ApiResponse.success("Current placeholder user loaded successfully.", userService.getCurrentUser(authentication));
    }
}
