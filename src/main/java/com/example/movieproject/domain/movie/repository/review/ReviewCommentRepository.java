package com.example.movieproject.domain.movie.repository.review;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.review.Review;
import com.example.movieproject.domain.movie.entity.review.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByUser(User user);
    List<ReviewComment> findByReview(Review review);
}