package com.example.movieproject.domain.movie.dto;

public record ReviewRequest(
        String content,
        Double rating,
        Integer movieId,
        Long emotionId
) {}
