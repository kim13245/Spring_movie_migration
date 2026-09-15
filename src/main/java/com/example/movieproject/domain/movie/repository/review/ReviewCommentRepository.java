package com.example.movieproject.domain.movie.repository.review;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.review.Review;
import com.example.movieproject.domain.movie.entity.review.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByUser(User user);
    List<ReviewComment> findByReview(Review review);
    long countByUser(User user);

    @Query("SELECT rc FROM ReviewComment rc JOIN FETCH rc.user WHERE rc.review = :review")
    List<ReviewComment> findByReviewWithUser(@Param("review") Review review);

    @Query("SELECT rc FROM ReviewComment rc JOIN FETCH rc.user WHERE rc.review.id IN :reviewIds")
    List<ReviewComment> findByReviewIdInWithUser(@Param("reviewIds") List<Long> reviewIds);
}