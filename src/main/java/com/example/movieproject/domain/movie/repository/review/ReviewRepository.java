package com.example.movieproject.domain.movie.repository.review;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.core.Movie;
import com.example.movieproject.domain.movie.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
    List<Review> findByMovie(Movie movie);
    List<Review> findByMovieId(Integer movieId);
    Optional<Review> findByUserAndMovie(User user, Movie movie);
    boolean existsByUserAndMovie(User user, Movie movie);
    long countByUser(User user);

    @org.springframework.data.jpa.repository.Query("SELECT AVG(r.rating) FROM Review r WHERE r.user = :user")
    Double findAverageRatingByUser(@org.springframework.data.repository.query.Param("user") User user);
}