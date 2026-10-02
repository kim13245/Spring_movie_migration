package com.example.movieproject.domain.movie.service;

import com.example.movieproject.common.exception.CustomException;
import com.example.movieproject.common.exception.ErrorCode;
import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import com.example.movieproject.domain.movie.dto.CollectionRequest;
import com.example.movieproject.domain.movie.dto.CollectionResponse;
import com.example.movieproject.domain.movie.dto.MovieSimpleResponse;
import com.example.movieproject.domain.movie.entity.core.Movie;
import com.example.movieproject.domain.movie.entity.playlist.Collection;
import com.example.movieproject.domain.movie.repository.core.MovieRepository;
import com.example.movieproject.domain.movie.repository.playlist.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public CollectionResponse createCollection(String email, CollectionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Collection collection = Collection.builder()
                .title(request.title())
                .user(user)
                .build();

        Collection savedCollection = collectionRepository.save(collection);

        return buildCollectionResponse(savedCollection);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse getCollection(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        return buildCollectionResponse(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionResponse> getCollectionsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        List<Collection> collections = collectionRepository.findByUserWithMovies(user);

        return collections.stream()
                .map(this::buildCollectionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCollectionTitle(String email, Long collectionId, String title) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 소유자 권한 확인
        if (!collection.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        collection.updateTitle(title);
    }

    @Override
    @Transactional
    public void deleteCollection(String email, Long collectionId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 소유자 권한 확인
        if (!collection.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        collectionRepository.delete(collection);
    }

    @Override
    @Transactional
    public void addMoviesToCollection(String email, Long collectionId, List<Integer> movieIds) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 소유자 권한 확인
        if (!collection.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Set<Movie> moviesToAdd = new HashSet<>(movieRepository.findAllById(movieIds));
        collection.addMovies(moviesToAdd);
    }

    @Override
    @Transactional
    public void removeMoviesFromCollection(String email, Long collectionId, List<Integer> movieIds) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 소유자 권한 확인
        if (!collection.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Set<Movie> moviesToRemove = new HashSet<>(movieRepository.findAllById(movieIds));
        collection.removeMovies(moviesToRemove);
    }

    private CollectionResponse buildCollectionResponse(Collection collection) {
        List<MovieSimpleResponse> movieResponses = collection.getMovies().stream()
                .map(movie -> MovieSimpleResponse.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .posterPath(movie.getPosterPath())
                        .build())
                .collect(Collectors.toList());

        return CollectionResponse.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .voteAverage(collection.getVoteAverage())
                .userId(collection.getUser().getId())
                .movies(movieResponses)
                .build();
    }
}
