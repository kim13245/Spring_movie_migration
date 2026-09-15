package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

@Builder
public record CrewResponse(
        Long id,
        String name,
        String department,
        Integer personId,
        String profilePath
) {
}
