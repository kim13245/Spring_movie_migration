package com.example.movieproject.external.tmdb.dto;

import lombok.Data;

import java.util.List;

@Data
public class TmdbCreditsResponse {
    private Integer id;
    private List<TmdbCastDto> cast;
    private List<TmdbCrewDto> crew;
}
