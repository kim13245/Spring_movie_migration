package com.example.movieproject.external.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TmdbCastDto {
    private Integer id; // person id
    private String name;
    private String character;

    @JsonProperty("profile_path")
    private String profilePath;
}
