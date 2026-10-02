package com.example.movieproject.domain.movie.controller;

import com.example.movieproject.common.response.ApiResponse;
import com.example.movieproject.domain.movie.dto.MovieDetailWithReviewsResponse;
import com.example.movieproject.domain.movie.dto.MovieListResponse;
import com.example.movieproject.domain.movie.service.MovieService;
import com.example.movieproject.external.tmdb.dto.TmdbSearchResponse;
import com.example.movieproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // 영화 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieListResponse>>> getAllMovies() {
        List<MovieListResponse> responses = movieService.getAllMovies();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 영화 상세 조회
    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieDetailWithReviewsResponse>> getMovieDetail(
            @PathVariable Integer movieId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        MovieDetailWithReviewsResponse response = movieService.getMovieDetail(movieId, email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 영화 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<TmdbSearchResponse>> searchMovie(
            @RequestParam String title) {
        TmdbSearchResponse response = movieService.searchMovie(title);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 감정별 영화 목록 조회
    @GetMapping("/emotions/{emotionName}")
    public ResponseEntity<ApiResponse<List<MovieListResponse>>> getMoviesByEmotion(
            @PathVariable String emotionName) {
        List<MovieListResponse> responses = movieService.getMoviesByEmotion(emotionName);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
