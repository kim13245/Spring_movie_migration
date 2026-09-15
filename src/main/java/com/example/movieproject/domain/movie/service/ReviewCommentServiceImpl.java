package com.example.movieproject.domain.movie.service;

import com.example.movieproject.common.exception.CustomException;
import com.example.movieproject.common.exception.ErrorCode;
import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import com.example.movieproject.domain.movie.dto.ReviewCommentRequest;
import com.example.movieproject.domain.movie.dto.ReviewCommentResponse;
import com.example.movieproject.domain.movie.entity.review.Review;
import com.example.movieproject.domain.movie.entity.review.ReviewComment;
import com.example.movieproject.domain.movie.repository.review.ReviewCommentRepository;
import com.example.movieproject.domain.movie.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewCommentServiceImpl implements ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewCommentResponse createComment(String email, Long reviewId, ReviewCommentRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        ReviewComment comment = ReviewComment.builder()
                .review(review)
                .user(user)
                .content(request.content())
                .build();

        ReviewComment savedComment = reviewCommentRepository.save(comment);

        return buildCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewCommentResponse> getCommentsByReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        List<ReviewComment> comments = reviewCommentRepository.findByReview(review);

        return comments.stream()
                .map(this::buildCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewCommentResponse updateComment(String email, Long commentId, ReviewCommentRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 작성자 권한 확인
        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        comment.updateContent(request.content());

        return buildCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(String email, Long commentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 작성자 권한 확인
        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        reviewCommentRepository.delete(comment);
    }

    private ReviewCommentResponse buildCommentResponse(ReviewComment comment) {
        return ReviewCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getUser().getNickname())
                .userId(comment.getUser().getId())
                .reviewId(comment.getReview().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
