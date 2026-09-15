package com.example.movieproject.external.tmdb.dto;

import lombok.Data;

import java.util.List;

@Data
public class TmdbSearchResponse {
    private Integer page;
    private List<TmdbMovieDto> results;

    @com.fasterxml.jackson.annotation.JsonProperty("total_pages")
    private Integer totalPages;

    @com.fasterxml.jackson.annotation.JsonProperty("total_results")
    private Integer totalResults;
}
