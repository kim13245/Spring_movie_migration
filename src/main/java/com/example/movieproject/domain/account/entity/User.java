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

    // 탈퇴 여부 - 소프트 딜리트
    @Column(nullable = false)
    private boolean active = true;

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

    // 탈퇴 처리 - 실제 데이터는 남기고 계정만 비활성화, 닉네임은 익명화
    public void deactivate() {
        this.active = false;
        this.nickname = "탈퇴한 회원";
    }

    // 팔로우 추가
    public void follow(User targetUser) {
        this.followings.add(targetUser);
        targetUser.getFollowers().add(this);
    }

    // 언팔로우
    public void unfollow(User targetUser) {
        this.followings.remove(targetUser);
        targetUser.getFollowers().remove(this);
    }

    // 팔로우 여부 확인
    public boolean isFollowing(User targetUser) {
        return this.followings.contains(targetUser);
    }

    // 영화 찜하기
    public void keepMovie(Movie movie) {
        this.keptMovies.add(movie);
    }

    // 영화 찜 취소
    public void unkepMovie(Movie movie) {
        this.keptMovies.remove(movie);
    }

    // 영화 찜 여부 확인
    public boolean isKeptMovie(Movie movie) {
        return this.keptMovies.contains(movie);
    }

    public enum Role {
        USER, ADMIN
    }
}