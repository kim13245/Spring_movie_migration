package com.example.movieproject.domain.movie.repository.review;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.core.Movie;
import com.example.movieproject.domain.movie.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
    List<Review> findByMovie(Movie movie);
    List<Review> findByMovieId(Integer movieId);
    Optional<Review> findByUserAndMovie(User user, Movie movie);
    boolean existsByUserAndMovie(User user, Movie movie);
    long countByUser(User user);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.user = :user")
    Double findAverageRatingByUser(@Param("user") User user);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.emotion WHERE r.movie.id = :movieId")
    List<Review> findByMovieIdWithUserAndEmotion(@Param("movieId") Integer movieId);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.movie JOIN FETCH r.emotion")
    List<Review> findAllWithDetails();

    @Query("SELECT r.id, COUNT(l) FROM Review r LEFT JOIN r.likes l WHERE r.id IN :reviewIds GROUP BY r.id")
    List<Object[]> countLikesByReviewIds(@Param("reviewIds") List<Long> reviewIds);

    @Query("SELECT r.id FROM Review r JOIN r.likes l WHERE r.id IN :reviewIds AND l.id = :userId")
    List<Long> findReviewIdsLikedByUser(@Param("reviewIds") List<Long> reviewIds, @Param("userId") Long userId);
}