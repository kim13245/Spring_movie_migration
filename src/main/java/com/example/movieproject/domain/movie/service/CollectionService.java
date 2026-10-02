package com.example.movieproject.domain.movie.service;

import com.example.movieproject.domain.movie.dto.CollectionRequest;
import com.example.movieproject.domain.movie.dto.CollectionResponse;

import java.util.List;

public interface CollectionService {
    CollectionResponse createCollection(String email, CollectionRequest request);
    CollectionResponse getCollection(Long collectionId);
    List<CollectionResponse> getCollectionsByUser(Long userId);
    void updateCollectionTitle(String email, Long collectionId, String title);
    void deleteCollection(String email, Long collectionId);
    void addMoviesToCollection(String email, Long collectionId, List<Integer> movieIds);
    void removeMoviesFromCollection(String email, Long collectionId, List<Integer> movieIds);
}
