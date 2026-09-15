package com.example.movieproject.domain.movie.entity.core;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {

    @Id
    private Integer id; // TMDB 영화 ID

    @Column(nullable = false)
    private String title;

    private String belongToCollection;
    private String originalTitle;
    private LocalDate releaseDate;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private Integer runtime;
    private Double popularity;
    private Double voteAverage;

    @Column(nullable = false)
    private Integer voteCount = 0;

    private String posterPath;
    private String backdropPath;
    private Long budget;
    private Long revenue;

    @Column(nullable = false)
    private Boolean adult = false;

    private String status;
    private String homepage;
    private String imdbId;
    private String tagline;
    private String originCountry;
    private String spokenLanguages;

    @Column(nullable = false)
    private Double userRating = 0.0;

    private String trailer;

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cast> cast = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Crew> crew = new ArrayList<>();

    @Builder
    public Movie(Integer id, String title, String originalTitle, LocalDate releaseDate,
                 String overview, Integer runtime, Double popularity, Double voteAverage,
                 Integer voteCount, String posterPath, String backdropPath, Long budget,
                 Long revenue, Boolean adult, String status, String homepage, String imdbId,
                 String tagline, String originCountry, String spokenLanguages, String trailer) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.runtime = runtime;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount != null ? voteCount : 0;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.budget = budget;
        this.revenue = revenue;
        this.adult = adult != null ? adult : false;
        this.status = status;
        this.homepage = homepage;
        this.imdbId = imdbId;
        this.tagline = tagline;
        this.originCountry = originCountry;
        this.spokenLanguages = spokenLanguages;
        this.trailer = trailer;
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }

    public void addCast(Cast castMember) {
        this.cast.add(castMember);
    }

    public void addCrew(Crew crewMember) {
        this.crew.add(crewMember);
    }
}