package com.example.movieproject.domain.movie.service;

import com.example.movieproject.common.exception.CustomException;
import com.example.movieproject.common.exception.ErrorCode;
import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import com.example.movieproject.domain.movie.dto.ReviewRequest;
import com.example.movieproject.domain.movie.dto.ReviewResponse;
import com.example.movieproject.domain.movie.entity.core.Emotion;
import com.example.movieproject.domain.movie.entity.core.Movie;
import com.example.movieproject.domain.movie.entity.review.Review;
import com.example.movieproject.domain.movie.repository.core.EmotionRepository;
import com.example.movieproject.domain.movie.repository.core.MovieRepository;
import com.example.movieproject.domain.movie.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final EmotionRepository emotionRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(String email, ReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Emotion emotion = emotionRepository.findById(request.emotionId())
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 중복 리뷰 검증
        if (reviewRepository.existsByUserAndMovie(user, movie)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        Review review = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .user(user)
                .movie(movie)
                .emotion(emotion)
                .build();

        Review savedReview = reviewRepository.save(review);

        return buildReviewResponse(savedReview, user);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        User currentUser = email != null ? userRepository.findByEmail(email).orElse(null) : null;

        return buildReviewResponse(review, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews(String email) {
        List<Review> reviews = reviewRepository.findAll();
        User currentUser = email != null ? userRepository.findByEmail(email).orElse(null) : null;

        return reviews.stream()
                .map(review -> buildReviewResponse(review, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(String email, Long reviewId, ReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 작성자 권한 확인
        if (!review.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        review.updateContent(request.content(), request.rating());

        return buildReviewResponse(review, user);
    }

    @Override
    @Transactional
    public void deleteReview(String email, Long reviewId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 작성자 권한 확인
        if (!review.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public void toggleLike(String email, Long reviewId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        review.toggleLike(user);
    }

    private ReviewResponse buildReviewResponse(Review review, User currentUser) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreateReview())
                .updatedAt(review.getUpdateReview())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .nickname(review.getUser().getNickname())
                .movieId(review.getMovie().getId())
                .movieTitle(review.getMovie().getTitle())
                .emotionId(review.getEmotion().getId())
                .emotionName(review.getEmotion().getName())
                .likesCount(review.getLikesCount())
                .isLiked(currentUser != null && review.isLikedBy(currentUser))
                .comments(List.of()) // TODO: ReviewComment 구현 후 추가
                .build();
    }
}
