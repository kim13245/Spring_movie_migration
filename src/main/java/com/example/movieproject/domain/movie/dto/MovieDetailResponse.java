package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MovieDetailResponse(
        Integer id,
        String title,
        String originalTitle,
        LocalDate releaseDate,
        String overview,
        Integer runtime,
        Double popularity,
        Double voteAverage,
        Integer voteCount,
        String posterPath,
        String backdropPath,
        Long budget,
        Long revenue,
        Boolean adult,
        String status,
        String homepage,
        String imdbId,
        String tagline,
        String originCountry,
        String spokenLanguages,
        Double userRating,
        String trailer,
        List<GenreResponse> genres
) {
}
