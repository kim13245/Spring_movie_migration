package com.example.movieproject.external.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TmdbProductionCountryDto {
    @JsonProperty("iso_3166_1")
    private String iso31661;

    private String name;
}
