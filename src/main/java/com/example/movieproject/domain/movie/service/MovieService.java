package com.example.movieproject.domain.movie.service;

import com.example.movieproject.domain.movie.dto.MovieDetailWithReviewsResponse;
import com.example.movieproject.domain.movie.dto.MovieListResponse;
import com.example.movieproject.external.tmdb.dto.TmdbSearchResponse;

import java.util.List;

public interface MovieService {
    List<MovieListResponse> getAllMovies();
    MovieDetailWithReviewsResponse getMovieDetail(Integer movieId, String email);
    TmdbSearchResponse searchMovie(String title);
    List<MovieListResponse> getMoviesByEmotion(String emotionName);
}
