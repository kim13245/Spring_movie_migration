package com.example.movieproject.domain.account.entity;

import com.example.movieproject.domain.movie.entity.core.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname = "크와와와왕";

    private String userProfile;
    private String userIntro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @ManyToMany
    @JoinTable(
            name = "user_kept_movie",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> keptMovies = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> followings = new HashSet<>();

    @ManyToMany(mappedBy = "followings")
    private Set<User> followers = new HashSet<>();

    @Builder
    public User(String username, String password, String email, String nickname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = (nickname != null) ? nickname : "크와와와왕";
        this.role = Role.USER;
    }

    public void updateProfile(String userProfile, String email, String nickname, String userIntro) {
        if (userProfile != null) this.userProfile = userProfile;
        if (email != null) this.email = email;
        if (nickname != null) this.nickname = nickname;
        if (userIntro != null) this.userIntro = userIntro;
    }

    public enum Role {
        USER, ADMIN
    }
}