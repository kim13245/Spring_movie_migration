package com.example.movieproject.domain.movie.entity.core;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre {

    @Id
    private Integer id; // TMDB 장르 ID

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private Set<Emotion> emotions = new HashSet<>();

    @Builder
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}