package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewCommentResponse(
        Long id,
        String content,
        String nickname,
        Long userId,
        Long reviewId,
        LocalDateTime createdAt
) {}
