package com.example.movieproject.external.tmdb;

import com.example.movieproject.external.tmdb.dto.TmdbCreditsResponse;
import com.example.movieproject.external.tmdb.dto.TmdbMovieDetailResponse;
import com.example.movieproject.external.tmdb.dto.TmdbSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class TmdbClient {

    private final RestClient restClient;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    private static final String LANGUAGE = "ko-KR";

    public TmdbMovieDetailResponse getMovieDetails(Integer movieId) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/movie/" + movieId)
                .queryParam("api_key", apiKey)
                .queryParam("language", LANGUAGE)
                .build()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(TmdbMovieDetailResponse.class);
    }

    public TmdbCreditsResponse getCredits(Integer movieId) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/movie/" + movieId + "/credits")
                .queryParam("api_key", apiKey)
                .queryParam("language", LANGUAGE)
                .build()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(TmdbCreditsResponse.class);
    }

    public TmdbSearchResponse searchMovie(String title) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", title)
                .queryParam("language", LANGUAGE)
                .build()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(TmdbSearchResponse.class);
    }
}
