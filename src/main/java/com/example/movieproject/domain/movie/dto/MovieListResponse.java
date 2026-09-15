package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MovieListResponse(
        Integer id,
        String title,
        String posterPath,
        LocalDate releaseDate,
        Double voteAverage,
        Double popularity
) {
}
