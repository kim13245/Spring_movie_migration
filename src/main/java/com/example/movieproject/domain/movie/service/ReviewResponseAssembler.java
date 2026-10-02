package com.example.movieproject.domain.movie.service;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.dto.ReviewCommentResponse;
import com.example.movieproject.domain.movie.dto.ReviewResponse;
import com.example.movieproject.domain.movie.entity.review.Review;
import com.example.movieproject.domain.movie.entity.review.ReviewComment;
import com.example.movieproject.domain.movie.repository.review.ReviewCommentRepository;
import com.example.movieproject.domain.movie.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// 리뷰 -> ReviewResponse 조립 로직 (댓글/좋아요 배치 조회 포함)을 한 곳에 모아
// ReviewServiceImpl/MovieServiceImpl이 동일한 로직을 각자 들고 있지 않도록 함
@Component
@RequiredArgsConstructor
public class ReviewResponseAssembler {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;

    public ReviewResponse toResponse(Review review, User currentUser) {
        List<ReviewComment> comments = reviewCommentRepository.findByReview(review);
        long likesCount = review.getLikesCount();
        boolean isLiked = currentUser != null && review.isLikedBy(currentUser);
        return assemble(review, comments, likesCount, isLiked);
    }

    public List<ReviewResponse> toResponses(List<Review> reviews, User currentUser) {
        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();

        Map<Long, List<ReviewComment>> commentsByReview = reviewIds.isEmpty()
                ? Map.of()
                : reviewCommentRepository.findByReviewIdInWithUser(reviewIds).stream()
                        .collect(Collectors.groupingBy(comment -> comment.getReview().getId()));

        Map<Long, Long> likesCountByReview = reviewIds.isEmpty()
                ? Map.of()
                : reviewRepository.countLikesByReviewIds(reviewIds).stream()
                        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Set<Long> likedReviewIds = (currentUser != null && !reviewIds.isEmpty())
                ? new HashSet<>(reviewRepository.findReviewIdsLikedByUser(reviewIds, currentUser.getId()))
                : Set.of();

        return reviews.stream()
                .map(review -> assemble(review,
                        commentsByReview.getOrDefault(review.getId(), List.of()),
                        likesCountByReview.getOrDefault(review.getId(), 0L),
                        likedReviewIds.contains(review.getId())))
                .toList();
    }

    private ReviewResponse assemble(Review review, List<ReviewComment> comments, long likesCount, boolean isLiked) {
        List<ReviewCommentResponse> commentResponses = comments.stream()
                .map(comment -> ReviewCommentResponse.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreateComment())
                        .updatedAt(comment.getUpdateComment())
                        .userId(comment.getUser().getId())
                        .username(comment.getUser().getUsername())
                        .nickname(comment.getUser().getNickname())
                        .reviewId(review.getId())
                        .build())
                .collect(Collectors.toList());

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
                .emotionId(review.getEmotion() != null ? review.getEmotion().getId() : null)
                .emotionName(review.getEmotion() != null ? review.getEmotion().getName() : null)
                .likesCount((int) likesCount)
                .isLiked(isLiked)
                .comments(commentResponses)
                .build();
    }
}
