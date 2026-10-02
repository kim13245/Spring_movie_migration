package com.example.movieproject.domain.account.service;

import com.example.movieproject.domain.account.dto.FollowResponse;
import com.example.movieproject.domain.account.dto.ProfileResponse;
import com.example.movieproject.domain.account.dto.ProfileUpdateRequest;

public interface MemberService {
    void signout(String email);
    ProfileResponse getMyProfile(String email);
    ProfileResponse getUserProfile(Long userId);
    void updateProfile(String email, ProfileUpdateRequest request);
    FollowResponse toggleFollow(String email, Long targetUserId);
    void toggleKeepMovie(String email, Integer movieId);
}