package com.example.movieproject.external.tmdb;

import com.example.movieproject.external.tmdb.dto.TmdbCreditsResponse;
import com.example.movieproject.external.tmdb.dto.TmdbMovieDetailResponse;
import com.example.movieproject.external.tmdb.dto.TmdbSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class TmdbClient {

    private final RestTemplate restTemplate;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    private static final String LANGUAGE = "ko-KR";

    public TmdbMovieDetailResponse getMovieDetails(Integer movieId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/" + movieId)
                .queryParam("api_key", apiKey)
                .queryParam("language", LANGUAGE)
                .toUriString();

        return restTemplate.getForObject(url, TmdbMovieDetailResponse.class);
    }

    public TmdbCreditsResponse getCredits(Integer movieId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/" + movieId + "/credits")
                .queryParam("api_key", apiKey)
                .queryParam("language", LANGUAGE)
                .toUriString();

        return restTemplate.getForObject(url, TmdbCreditsResponse.class);
    }

    public TmdbSearchResponse searchMovie(String title) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", title)
                .queryParam("language", LANGUAGE)
                .toUriString();

        return restTemplate.getForObject(url, TmdbSearchResponse.class);
    }
}
