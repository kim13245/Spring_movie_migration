package com.example.movieproject.domain.movie.controller;

import com.example.movieproject.common.response.ApiResponse;
import com.example.movieproject.domain.movie.dto.CollectionRequest;
import com.example.movieproject.domain.movie.dto.CollectionResponse;
import com.example.movieproject.domain.movie.service.CollectionService;
import com.example.movieproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    // 플레이리스트 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CollectionResponse>> createCollection(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CollectionRequest request) {
        CollectionResponse response = collectionService.createCollection(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 플레이리스트 단건 조회
    @GetMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<CollectionResponse>> getCollection(
            @PathVariable Long collectionId) {
        CollectionResponse response = collectionService.getCollection(collectionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 특정 사용자의 플레이리스트 목록 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<CollectionResponse>>> getCollectionsByUser(
            @PathVariable Long userId) {
        List<CollectionResponse> responses = collectionService.getCollectionsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 플레이리스트 제목 수정
    @PutMapping("/{collectionId}/title")
    public ResponseEntity<ApiResponse<Void>> updateCollectionTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long collectionId,
            @RequestBody CollectionRequest request) {
        collectionService.updateCollectionTitle(userDetails.getUsername(), collectionId, request.title());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 플레이리스트 삭제
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long collectionId) {
        collectionService.deleteCollection(userDetails.getUsername(), collectionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 플레이리스트에 영화 추가
    @PostMapping("/{collectionId}/movies")
    public ResponseEntity<ApiResponse<Void>> addMoviesToCollection(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long collectionId,
            @RequestBody List<Integer> movieIds) {
        collectionService.addMoviesToCollection(userDetails.getUsername(), collectionId, movieIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 플레이리스트에서 영화 제거
    @DeleteMapping("/{collectionId}/movies")
    public ResponseEntity<ApiResponse<Void>> removeMoviesFromCollection(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long collectionId,
            @RequestBody List<Integer> movieIds) {
        collectionService.removeMoviesFromCollection(userDetails.getUsername(), collectionId, movieIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
