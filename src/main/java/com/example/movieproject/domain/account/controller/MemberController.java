package com.example.movieproject.domain.account.controller;

import com.example.movieproject.common.response.ApiResponse;
import com.example.movieproject.domain.account.dto.FollowResponse;
import com.example.movieproject.domain.account.dto.ProfileResponse;
import com.example.movieproject.domain.account.dto.ProfileUpdateRequest;
import com.example.movieproject.domain.account.service.MemberService;
import com.example.movieproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> signout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.signout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ProfileResponse profile = memberService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getUserProfile(@PathVariable Long userId) {
        ProfileResponse profile = memberService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProfileUpdateRequest request) {
        memberService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<FollowResponse>> toggleFollow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userId) {
        FollowResponse response = memberService.toggleFollow(userDetails.getUsername(), userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/keep/{movieId}")
    public ResponseEntity<ApiResponse<Void>> toggleKeepMovie(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer movieId) {
        memberService.toggleKeepMovie(userDetails.getUsername(), movieId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}