package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MovieDetailWithReviewsResponse(
        MovieDetailResponse movie,
        List<ReviewResponse> reviews,
        CreditsResponse credits
) {
}
