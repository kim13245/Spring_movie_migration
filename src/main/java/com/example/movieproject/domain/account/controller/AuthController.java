package com.example.movieproject.domain.account.controller;

import com.example.movieproject.common.response.ApiResponse;
import com.example.movieproject.domain.account.dto.LoginRequest;
import com.example.movieproject.domain.account.dto.SignUpRequest;
import com.example.movieproject.domain.account.dto.TokenResponse;
import com.example.movieproject.domain.account.service.AuthService;
import com.example.movieproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestBody RefreshRequest request) {
        TokenResponse tokens = authService.reissue(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    public record RefreshRequest(String refreshToken) {}
}