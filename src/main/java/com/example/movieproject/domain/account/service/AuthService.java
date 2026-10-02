package com.example.movieproject.domain.account.service;

import com.example.movieproject.domain.account.dto.LoginRequest;
import com.example.movieproject.domain.account.dto.SignUpRequest;
import com.example.movieproject.domain.account.dto.TokenResponse;

public interface AuthService {
    void signUp(SignUpRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse reissue(String refreshToken);
    void logout(String email);
}