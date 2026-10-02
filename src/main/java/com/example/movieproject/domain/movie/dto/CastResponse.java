package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

@Builder
public record CastResponse(
        Long id,
        String name,
        String character,
        Integer personId,
        String profilePath
) {
}
