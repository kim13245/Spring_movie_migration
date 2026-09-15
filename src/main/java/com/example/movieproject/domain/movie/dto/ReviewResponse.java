package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReviewResponse(
        Long id,
        String content,
        Double rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        String username,
        String nickname,
        Integer movieId,
        String movieTitle,
        Long emotionId,
        String emotionName,
        Integer likesCount,
        Boolean isLiked,
        List<ReviewCommentResponse> comments
) {}
