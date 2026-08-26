package com.example.movieproject.domain.movie.entity.review;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.core.Emotion;
import com.example.movieproject.domain.movie.entity.core.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @ManyToMany
    @JoinTable(
            name = "review_likes",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes = new HashSet<>();

    @Builder
    public Review(String content, Double rating, User user, Movie movie, Emotion emotion) {
        this.content = content;
        this.rating = rating != null ? rating : 0.0;
        this.user = user;
        this.movie = movie;
        this.emotion = emotion;
    }

    @PrePersist
    protected void onCreate() {
        this.createReview = LocalDateTime.now();
    }

    public void updateContent(String content, Double rating) {
        if (content != null) this.content = content;
        if (rating != null) this.rating = rating;
    }

    public int getLikesCount() {
        return likes.size();
    }
}