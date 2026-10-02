package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

@Builder
public record GenreResponse(
        Integer id,
        String name
) {
}
