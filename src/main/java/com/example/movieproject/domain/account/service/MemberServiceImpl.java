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
import com.example.movieproject.security.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public void signout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        user.deactivate();
        refreshTokenService.delete(email);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        return buildProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        return buildProfileResponse(user);
    }

    @Override
    @Transactional
    public void updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        user.updateProfile(request.userProfile(), request.email(), request.nickname(), request.userIntro());
    }

    @Override
    @Transactional
    public FollowResponse toggleFollow(String email, Long targetUserId) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        if (currentUser.equals(targetUser)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        boolean isFollowing;
        String message;

        if (currentUser.isFollowing(targetUser)) {
            currentUser.unfollow(targetUser);
            isFollowing = false;
            message = "팔로우를 취소했습니다.";
        } else {
            currentUser.follow(targetUser);
            isFollowing = true;
            message = "팔로우했습니다.";
        }

        return new FollowResponse(message, isFollowing);
    }

    @Override
    @Transactional
    public void toggleKeepMovie(String email, Integer movieId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        if (user.isKeptMovie(movie)) {
            user.unkepMovie(movie);
        } else {
            user.keepMovie(movie);
        }
    }

    private ProfileResponse buildProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userProfile(user.getUserProfile())
                .userIntro(user.getUserIntro())
                .followersCount(user.getFollowers().size())
                .followingsCount(user.getFollowings().size())
                .keptMoviesCount(user.getKeptMovies().size())
                .reviewCount(0) // TODO: Review 도메인 구현 후 추가
                .reviewCommentCount(0) // TODO: ReviewComment 도메인 구현 후 추가
                .ratingAverage(0.0) // TODO: Review 도메인 구현 후 추가
                .followersNames(user.getFollowers().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toList()))
                .followingsNames(user.getFollowings().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toList()))
                .build();
    }
}