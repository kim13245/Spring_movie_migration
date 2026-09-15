package com.example.movieproject.domain.movie.entity.review;

import com.example.movieproject.domain.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createComment;

    private LocalDateTime updateComment;

    @Builder
    public ReviewComment(Review review, User user, String content) {
        this.review = review;
        this.user = user;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createComment = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateComment = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
    }
}