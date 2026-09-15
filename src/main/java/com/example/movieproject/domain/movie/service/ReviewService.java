package com.example.movieproject.domain.movie.service;

import com.example.movieproject.domain.movie.dto.ReviewRequest;
import com.example.movieproject.domain.movie.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(String email, ReviewRequest request);
    ReviewResponse getReview(Long reviewId, String email);
    List<ReviewResponse> getAllReviews(String email);
    ReviewResponse updateReview(String email, Long reviewId, ReviewRequest request);
    void deleteReview(String email, Long reviewId);
    void toggleLike(String email, Long reviewId);
}
