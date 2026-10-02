package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CollectionResponse(
        Long id,
        String title,
        Double voteAverage,
        Long userId,
        List<MovieSimpleResponse> movies
) {}
