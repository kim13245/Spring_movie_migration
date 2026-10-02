package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        String username,
        String nickname,
        Long reviewId
) {}
