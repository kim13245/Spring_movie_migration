package com.example.movieproject.domain.account.repository;

import com.example.movieproject.domain.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.followers LEFT JOIN FETCH u.followings WHERE u.email = :email")
    Optional<User> findByEmailWithFollows(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.followers LEFT JOIN FETCH u.followings WHERE u.id = :id")
    Optional<User> findByIdWithFollows(@Param("id") Long id);

    @Query("SELECT COUNT(m) FROM User u JOIN u.keptMovies m WHERE u.id = :userId")
    long countKeptMoviesByUserId(@Param("userId") Long userId);
}