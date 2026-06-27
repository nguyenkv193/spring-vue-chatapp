package com.example.chatapp.auth.controller;

import com.example.chatapp.auth.dto.AuthTokenResponse;
import com.example.chatapp.auth.dto.LoginRequest;
import com.example.chatapp.auth.service.AuthService;
import com.example.chatapp.common.constants.ApplicationConstants;
import com.example.chatapp.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApplicationConstants.API_V1 + "/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(
                "Placeholder login issued a dev token. Replace with real credential validation in the next phase.",
                authService.login(request)
        );
    }
}
