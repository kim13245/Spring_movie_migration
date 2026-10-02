package com.example.movieproject.domain.movie.entity.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String character;

    @Builder
    public Cast(Movie movie, Person person, String name, String character) {
        this.movie = movie;
        this.person = person;
        this.name = name;
        this.character = character;
    }
}