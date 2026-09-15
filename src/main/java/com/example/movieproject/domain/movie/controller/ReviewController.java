package com.example.movieproject.domain.movie.controller;

import com.example.movieproject.common.response.ApiResponse;
import com.example.movieproject.domain.movie.dto.ReviewCommentRequest;
import com.example.movieproject.domain.movie.dto.ReviewCommentResponse;
import com.example.movieproject.domain.movie.dto.ReviewRequest;
import com.example.movieproject.domain.movie.dto.ReviewResponse;
import com.example.movieproject.domain.movie.service.ReviewCommentService;
import com.example.movieproject.domain.movie.service.ReviewService;
import com.example.movieproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 리뷰 단건 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        ReviewResponse response = reviewService.getReview(reviewId, email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 전체 리뷰 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        List<ReviewResponse> responses = reviewService.getAllReviews(email);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(userDetails.getUsername(), reviewId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(userDetails.getUsername(), reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 리뷰 좋아요 토글
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {
        reviewService.toggleLike(userDetails.getUsername(), reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 리뷰 댓글 작성
    @PostMapping("/{reviewId}/comments")
    public ResponseEntity<ApiResponse<ReviewCommentResponse>> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @RequestBody ReviewCommentRequest request) {
        ReviewCommentResponse response = reviewCommentService.createComment(
                userDetails.getUsername(), reviewId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 리뷰 댓글 조회
    @GetMapping("/{reviewId}/comments")
    public ResponseEntity<ApiResponse<List<ReviewCommentResponse>>> getComments(
            @PathVariable Long reviewId) {
        List<ReviewCommentResponse> responses = reviewCommentService.getCommentsByReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 리뷰 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<ReviewCommentResponse>> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId,
            @RequestBody ReviewCommentRequest request) {
        ReviewCommentResponse response = reviewCommentService.updateComment(
                userDetails.getUsername(), commentId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 리뷰 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        reviewCommentService.deleteComment(userDetails.getUsername(), commentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
