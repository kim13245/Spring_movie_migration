package com.example.movieproject.domain.movie.entity.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Person {

    @Id
    private Integer id; // TMDB 인물 ID

    @Column(nullable = false)
    private String name;

    private String profilePath;

    @Builder
    public Person(Integer id, String name, String profilePath) {
        this.id = id;
        this.name = name;
        this.profilePath = profilePath;
    }
}