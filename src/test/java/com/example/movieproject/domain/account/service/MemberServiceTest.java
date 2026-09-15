package com.example.movieproject.domain.account.service;

import com.example.movieproject.common.exception.CustomException;
import com.example.movieproject.common.exception.ErrorCode;
import com.example.movieproject.domain.account.dto.FollowResponse;
import com.example.movieproject.domain.account.dto.ProfileResponse;
import com.example.movieproject.domain.account.dto.ProfileUpdateRequest;
import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import com.example.movieproject.domain.movie.entity.core.Movie;
import com.example.movieproject.domain.movie.repository.core.MovieRepository;
import com.example.movieproject.domain.movie.repository.review.ReviewCommentRepository;
import com.example.movieproject.domain.movie.repository.review.ReviewRepository;
import com.example.movieproject.security.jwt.RefreshTokenService;
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
class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewCommentRepository reviewCommentRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("회원 탈퇴 성공")
    void signout_Success() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        memberService.signout(email);

        // then
        verify(refreshTokenService).delete(email);
    }

    @Test
    @DisplayName("내 프로필 조회 성공")
    void getMyProfile_Success() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(reviewRepository.countByUser(user)).willReturn(5L);
        given(reviewCommentRepository.countByUser(user)).willReturn(10L);
        given(reviewRepository.findAverageRatingByUser(user)).willReturn(4.5);

        // when
        ProfileResponse response = memberService.getMyProfile(email);

        // then
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.reviewCount()).isEqualTo(5);
        assertThat(response.reviewCommentCount()).isEqualTo(10);
        assertThat(response.ratingAverage()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("타인 프로필 조회 성공")
    void getUserProfile_Success() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.countByUser(user)).willReturn(3L);
        given(reviewCommentRepository.countByUser(user)).willReturn(7L);
        given(reviewRepository.findAverageRatingByUser(user)).willReturn(null);

        // when
        ProfileResponse response = memberService.getUserProfile(userId);

        // then
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.reviewCount()).isEqualTo(3);
        assertThat(response.reviewCommentCount()).isEqualTo(7);
        assertThat(response.ratingAverage()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile_Success() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "newtest@example.com",
                "새로운닉네임",
                "profile.jpg",
                "자기소개"
        );

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        memberService.updateProfile(email, request);

        // then
        assertThat(user.getUserProfile()).isEqualTo("profile.jpg");
        assertThat(user.getEmail()).isEqualTo("newtest@example.com");
        assertThat(user.getNickname()).isEqualTo("새로운닉네임");
        assertThat(user.getUserIntro()).isEqualTo("자기소개");
    }

    @Test
    @DisplayName("팔로우 성공")
    void toggleFollow_Follow_Success() {
        // given
        String email = "test@example.com";
        Long targetUserId = 2L;

        User currentUser = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        User targetUser = User.builder()
                .email("target@example.com")
                .username("targetuser")
                .password("password")
                .nickname("타겟유저")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(currentUser));
        given(userRepository.findById(targetUserId)).willReturn(Optional.of(targetUser));

        // when
        FollowResponse response = memberService.toggleFollow(email, targetUserId);

        // then
        assertThat(response.isFollowing()).isTrue();
        assertThat(response.message()).isEqualTo("팔로우했습니다.");
        assertThat(currentUser.getFollowings()).contains(targetUser);
        assertThat(targetUser.getFollowers()).contains(currentUser);
    }

    @Test
    @DisplayName("언팔로우 성공")
    void toggleFollow_Unfollow_Success() {
        // given
        String email = "test@example.com";
        Long targetUserId = 2L;

        User currentUser = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        User targetUser = User.builder()
                .email("target@example.com")
                .username("targetuser")
                .password("password")
                .nickname("타겟유저")
                .build();

        currentUser.follow(targetUser);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(currentUser));
        given(userRepository.findById(targetUserId)).willReturn(Optional.of(targetUser));

        // when
        FollowResponse response = memberService.toggleFollow(email, targetUserId);

        // then
        assertThat(response.isFollowing()).isFalse();
        assertThat(response.message()).isEqualTo("팔로우를 취소했습니다.");
        assertThat(currentUser.getFollowings()).doesNotContain(targetUser);
        assertThat(targetUser.getFollowers()).doesNotContain(currentUser);
    }

    @Test
    @DisplayName("팔로우 실패 - 자기 자신")
    void toggleFollow_Fail_SelfFollow() {
        // given
        String email = "test@example.com";
        Long targetUserId = 1L;

        User currentUser = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(currentUser));
        given(userRepository.findById(targetUserId)).willReturn(Optional.of(currentUser));

        // when & then
        assertThatThrownBy(() -> memberService.toggleFollow(email, targetUserId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
    }

    @Test
    @DisplayName("영화 찜하기 성공")
    void toggleKeepMovie_Keep_Success() {
        // given
        String email = "test@example.com";
        Integer movieId = 1;

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(movieId)
                .title("Test Movie")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));

        // when
        memberService.toggleKeepMovie(email, movieId);

        // then
        assertThat(user.getKeptMovies()).contains(movie);
    }

    @Test
    @DisplayName("영화 찜 취소 성공")
    void toggleKeepMovie_Unkeep_Success() {
        // given
        String email = "test@example.com";
        Integer movieId = 1;

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();

        Movie movie = Movie.builder()
                .id(movieId)
                .title("Test Movie")
                .build();

        user.keepMovie(movie);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));

        // when
        memberService.toggleKeepMovie(email, movieId);

        // then
        assertThat(user.getKeptMovies()).doesNotContain(movie);
    }
}
