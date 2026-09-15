package com.example.movieproject.external.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TmdbCrewDto {
    private Integer id; // person id
    private String name;
    private String department;

    @JsonProperty("profile_path")
    private String profilePath;
}
