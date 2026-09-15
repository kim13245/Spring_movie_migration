package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CreditsResponse(
        List<CastResponse> cast,
        List<CrewResponse> crew
) {
}
