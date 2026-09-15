package com.example.movieproject.domain.account.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProfileResponse(
        Long id,
        String username,
        String email,
        String nickname,
        String userProfile,
        String userIntro,
        Integer followersCount,
        Integer followingsCount,
        Integer keptMoviesCount,
        Integer reviewCount,
        Integer reviewCommentCount,
        Double ratingAverage,
        List<String> followersNames,
        List<String> followingsNames
) {}
