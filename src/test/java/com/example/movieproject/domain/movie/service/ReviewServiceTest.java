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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private EmotionRepository emotionRepository;

    @Mock
    private ReviewResponseAssembler reviewResponseAssembler;

    @Test
    @DisplayName("리뷰 작성 성공")
    void createReview_Success() {
        // given
        String email = "test@example.com";
        ReviewRequest request = new ReviewRequest("좋은 영화입니다", 4.5, 1, 1L);

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .build();

        Emotion emotion = Emotion.builder()
                .name("기쁨")
                .build();

        Review savedReview = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .user(user)
                .movie(movie)
                .emotion(emotion)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(movieRepository.findById(request.movieId())).willReturn(Optional.of(movie));
        given(emotionRepository.findById(request.emotionId())).willReturn(Optional.of(emotion));
        given(reviewRepository.existsByUserAndMovie(user, movie)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willReturn(savedReview);
        given(reviewResponseAssembler.toResponse(savedReview, user)).willReturn(
                ReviewResponse.builder().content(request.content()).rating(request.rating()).build());

        // when
        ReviewResponse response = reviewService.createReview(email, request);

        // then
        assertThat(response.content()).isEqualTo("좋은 영화입니다");
        assertThat(response.rating()).isEqualTo(4.5);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 중복 리뷰")
    void createReview_Fail_DuplicateReview() {
        // given
        String email = "test@example.com";
        ReviewRequest request = new ReviewRequest("좋은 영화입니다", 4.5, 1, 1L);

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .build();

        Emotion emotion = Emotion.builder()
                .name("기쁨")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(movieRepository.findById(request.movieId())).willReturn(Optional.of(movie));
        given(emotionRepository.findById(request.emotionId())).willReturn(Optional.of(emotion));
        given(reviewRepository.existsByUserAndMovie(user, movie)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(email, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_RESOURCE);
    }

    @Test
    @DisplayName("리뷰 단건 조회 성공")
    void getReview_Success() {
        // given
        Long reviewId = 1L;
        String email = "test@example.com";

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .build();

        Emotion emotion = Emotion.builder()
                .name("기쁨")
                .build();

        Review review = Review.builder()
                .content("좋은 영화입니다")
                .rating(4.5)
                .user(user)
                .movie(movie)
                .emotion(emotion)
                .build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(reviewResponseAssembler.toResponse(review, user)).willReturn(
                ReviewResponse.builder().content("좋은 영화입니다").rating(4.5).build());

        // when
        ReviewResponse response = reviewService.getReview(reviewId, email);

        // then
        assertThat(response.content()).isEqualTo("좋은 영화입니다");
        assertThat(response.rating()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_Success() {
        // given
        String email = "test@example.com";
        Long reviewId = 1L;
        ReviewRequest request = new ReviewRequest("수정된 내용", 5.0, 1, 1L);

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .build();

        Emotion emotion = Emotion.builder()
                .name("기쁨")
                .build();

        Review review = Review.builder()
                .content("원래 내용")
                .rating(4.0)
                .user(user)
                .movie(movie)
                .emotion(emotion)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(reviewResponseAssembler.toResponse(review, user)).willReturn(
                ReviewResponse.builder().content("수정된 내용").rating(5.0).build());

        // when
        ReviewResponse response = reviewService.updateReview(email, reviewId, request);

        // then
        assertThat(response.content()).isEqualTo("수정된 내용");
        assertThat(response.rating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 권한 없음")
    void updateReview_Fail_Forbidden() {
        // given
        String email = "test@example.com";
        Long reviewId = 1L;
        ReviewRequest request = new ReviewRequest("수정된 내용", 5.0, 1, 1L);

        User requestUser = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        User reviewOwner = User.builder()
                .email("owner@example.com")
                .username("owner")
                .password("password")
                .nickname("주인")
                .build();

        Movie movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .build();

        Emotion emotion = Emotion.builder()
                .name("기쁨")
                .build();

        Review review = Review.builder()
                .content("원래 내용")
                .rating(4.0)
                .user(reviewOwner)
                .movie(movie)
                .emotion(emotion)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(requestUser));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(email, reviewId, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_Success() {
        // given
        String email = "test@example.com";
        Long reviewId = 1L;

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .build();

        Emotion emotion = Emotion.builder()
                .name("기쁨")
                .build();

        Review review = Review.builder()
                .content("삭제할 리뷰")
                .rating(4.0)
                .user(user)
                .movie(movie)
                .emotion(emotion)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.deleteReview(email, reviewId);

        // then
        verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("리뷰 좋아요 토글 성공")
    void toggleLike_Success() {
        // given
        String email = "test@example.com";
        Long reviewId = 1L;

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .build();

        Emotion emotion = Emotion.builder()
                .name("기쁨")
                .build();

        Review review = Review.builder()
                .content("리뷰 내용")
                .rating(4.0)
                .user(user)
                .movie(movie)
                .emotion(emotion)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.toggleLike(email, reviewId);

        // then
        assertThat(review.isLikedBy(user)).isTrue();
    }
}
