package com.example.movieproject.external.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TmdbMovieDetailResponse {
    private Integer id;
    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    private String overview;
    private Integer runtime;
    private Double popularity;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    private Long budget;
    private Long revenue;
    private Boolean adult;
    private String status;
    private String homepage;

    @JsonProperty("imdb_id")
    private String imdbId;

    private String tagline;
    private List<TmdbGenreDto> genres;

    @JsonProperty("production_countries")
    private List<TmdbProductionCountryDto> productionCountries;

    @JsonProperty("spoken_languages")
    private List<TmdbSpokenLanguageDto> spokenLanguages;
}
