package com.example.movieproject.domain.movie.service;

import com.example.movieproject.domain.movie.dto.ReviewCommentRequest;
import com.example.movieproject.domain.movie.dto.ReviewCommentResponse;

import java.util.List;

public interface ReviewCommentService {
    ReviewCommentResponse createComment(String email, Long reviewId, ReviewCommentRequest request);
    List<ReviewCommentResponse> getCommentsByReview(Long reviewId);
    ReviewCommentResponse updateComment(String email, Long commentId, ReviewCommentRequest request);
    void deleteComment(String email, Long commentId);
}
