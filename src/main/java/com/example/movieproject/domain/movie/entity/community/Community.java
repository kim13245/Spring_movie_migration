package com.example.movieproject.domain.movie.entity.community;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.core.Movie;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Community(String title, String content, Movie movie, User user) {
        this.title = title;
        this.content = content;
        this.movie = movie;
        this.user = user;
    }

    public void update(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
    }
}