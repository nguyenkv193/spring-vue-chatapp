package com.example.chatapp.auth.service;

import com.example.chatapp.auth.dto.AuthTokenResponse;
import com.example.chatapp.auth.dto.LoginRequest;

public interface AuthService {

    AuthTokenResponse login(LoginRequest request);
}
