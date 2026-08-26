package com.example.movieproject.domain.movie.entity.playlist;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.core.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double voteAverage = 0.0;

    @ManyToMany
    @JoinTable(
            name = "collection_movie",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> movies = new HashSet<>();

    @Builder
    public Collection(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}