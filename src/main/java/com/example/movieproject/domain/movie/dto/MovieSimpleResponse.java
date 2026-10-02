package com.example.movieproject.domain.movie.dto;

import lombok.Builder;

@Builder
public record MovieSimpleResponse(
        Integer id,
        String title,
        String posterPath
) {}
