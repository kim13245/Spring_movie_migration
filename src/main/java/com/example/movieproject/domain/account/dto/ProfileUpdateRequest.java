package com.example.movieproject.domain.account.dto;

public record ProfileUpdateRequest(
        String email,
        String nickname,
        String userProfile,
        String userIntro
) {}
