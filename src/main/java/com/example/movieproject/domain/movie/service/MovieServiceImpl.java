package com.example.movieproject.domain.movie.service;

import com.example.movieproject.common.exception.CustomException;
import com.example.movieproject.common.exception.ErrorCode;
import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import com.example.movieproject.domain.movie.dto.*;
import com.example.movieproject.domain.movie.entity.core.*;
import com.example.movieproject.domain.movie.entity.review.Review;
import com.example.movieproject.domain.movie.repository.core.GenreRepository;
import com.example.movieproject.domain.movie.repository.core.MovieRepository;
import com.example.movieproject.domain.movie.repository.core.PersonRepository;
import com.example.movieproject.domain.movie.repository.review.ReviewRepository;
import com.example.movieproject.external.tmdb.TmdbClient;
import com.example.movieproject.external.tmdb.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final PersonRepository personRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TmdbClient tmdbClient;

    @Override
    @Transactional(readOnly = true)
    public List<MovieListResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::buildMovieListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieDetailWithReviewsResponse getMovieDetail(Integer movieId, String email) {
        // 영화가 DB에 없으면 TMDB에서 가져와서 저장
        Movie movie = movieRepository.findById(movieId).orElseGet(() -> {
            TmdbMovieDetailResponse tmdbMovie = tmdbClient.getMovieDetails(movieId);
            TmdbCreditsResponse credits = tmdbClient.getCredits(movieId);
            return createMovieFromTmdb(tmdbMovie, credits);
        });

        // 리뷰 조회
        List<Review> reviews = reviewRepository.findByMovieId(movieId);

        // 현재 사용자 조회 (로그인한 경우)
        User currentUser = null;
        if (email != null) {
            currentUser = userRepository.findByEmail(email).orElse(null);
        }

        // Credits 조회
        TmdbCreditsResponse creditsData = tmdbClient.getCredits(movieId);
        CreditsResponse credits = buildCreditsResponse(creditsData);

        MovieDetailResponse movieDetail = buildMovieDetailResponse(movie);
        List<ReviewResponse> reviewResponses = buildReviewResponses(reviews, currentUser);

        return MovieDetailWithReviewsResponse.builder()
                .movie(movieDetail)
                .reviews(reviewResponses)
                .credits(credits)
                .build();
    }

    @Override
    public TmdbSearchResponse searchMovie(String title) {
        return tmdbClient.searchMovie(title);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieListResponse> getMoviesByEmotion(String emotionName) {
        List<Movie> movies = genreRepository.findMoviesByEmotionName(emotionName);
        return movies.stream()
                .map(this::buildMovieListResponse)
                .collect(Collectors.toList());
    }

    private Movie createMovieFromTmdb(TmdbMovieDetailResponse tmdbMovie, TmdbCreditsResponse credits) {
        // Movie 생성
        Movie movie = Movie.builder()
                .id(tmdbMovie.getId())
                .title(tmdbMovie.getTitle())
                .originalTitle(tmdbMovie.getOriginalTitle())
                .releaseDate(tmdbMovie.getReleaseDate())
                .overview(tmdbMovie.getOverview())
                .runtime(tmdbMovie.getRuntime())
                .popularity(tmdbMovie.getPopularity())
                .voteAverage(tmdbMovie.getVoteAverage())
                .voteCount(tmdbMovie.getVoteCount())
                .posterPath(tmdbMovie.getPosterPath())
                .backdropPath(tmdbMovie.getBackdropPath())
                .budget(tmdbMovie.getBudget())
                .revenue(tmdbMovie.getRevenue())
                .adult(tmdbMovie.getAdult())
                .status(tmdbMovie.getStatus())
                .homepage(tmdbMovie.getHomepage())
                .imdbId(tmdbMovie.getImdbId())
                .tagline(tmdbMovie.getTagline())
                .originCountry(tmdbMovie.getProductionCountries() != null && !tmdbMovie.getProductionCountries().isEmpty()
                        ? tmdbMovie.getProductionCountries().get(0).getIso31661()
                        : null)
                .spokenLanguages(tmdbMovie.getSpokenLanguages() != null && !tmdbMovie.getSpokenLanguages().isEmpty()
                        ? tmdbMovie.getSpokenLanguages().get(0).getEnglishName()
                        : null)
                .build();

        // Genre 추가
        if (tmdbMovie.getGenres() != null) {
            for (TmdbGenreDto genreDto : tmdbMovie.getGenres()) {
                Genre genre = genreRepository.findById(genreDto.getId())
                        .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
                movie.addGenre(genre);
            }
        }

        Movie savedMovie = movieRepository.save(movie);

        // Cast 추가
        if (credits.getCast() != null) {
            for (TmdbCastDto castDto : credits.getCast()) {
                Person person = personRepository.findById(castDto.getId())
                        .orElseGet(() -> {
                            Person newPerson = Person.builder()
                                    .id(castDto.getId())
                                    .name(castDto.getName())
                                    .profilePath(castDto.getProfilePath())
                                    .build();
                            return personRepository.save(newPerson);
                        });

                Cast cast = Cast.builder()
                        .movie(savedMovie)
                        .person(person)
                        .name(castDto.getName())
                        .character(castDto.getCharacter())
                        .build();

                savedMovie.addCast(cast);
            }
        }

        // Crew 추가
        if (credits.getCrew() != null) {
            for (TmdbCrewDto crewDto : credits.getCrew()) {
                Person person = personRepository.findById(crewDto.getId())
                        .orElseGet(() -> {
                            Person newPerson = Person.builder()
                                    .id(crewDto.getId())
                                    .name(crewDto.getName())
                                    .profilePath(crewDto.getProfilePath())
                                    .build();
                            return personRepository.save(newPerson);
                        });

                Crew crew = Crew.builder()
                        .movie(savedMovie)
                        .person(person)
                        .department(crewDto.getDepartment())
                        .build();

                savedMovie.addCrew(crew);
            }
        }

        return savedMovie;
    }

    private MovieListResponse buildMovieListResponse(Movie movie) {
        return MovieListResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .releaseDate(movie.getReleaseDate())
                .voteAverage(movie.getVoteAverage())
                .popularity(movie.getPopularity())
                .build();
    }

    private MovieDetailResponse buildMovieDetailResponse(Movie movie) {
        List<GenreResponse> genres = movie.getGenres().stream()
                .map(genre -> GenreResponse.builder()
                        .id(genre.getId())
                        .name(genre.getName())
                        .build())
                .collect(Collectors.toList());

        return MovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .releaseDate(movie.getReleaseDate())
                .overview(movie.getOverview())
                .runtime(movie.getRuntime())
                .popularity(movie.getPopularity())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .posterPath(movie.getPosterPath())
                .backdropPath(movie.getBackdropPath())
                .budget(movie.getBudget())
                .revenue(movie.getRevenue())
                .adult(movie.getAdult())
                .status(movie.getStatus())
                .homepage(movie.getHomepage())
                .imdbId(movie.getImdbId())
                .tagline(movie.getTagline())
                .originCountry(movie.getOriginCountry())
                .spokenLanguages(movie.getSpokenLanguages())
                .userRating(movie.getUserRating())
                .trailer(movie.getTrailer())
                .genres(genres)
                .build();
    }

    private CreditsResponse buildCreditsResponse(TmdbCreditsResponse creditsData) {
        List<CastResponse> cast = creditsData.getCast() != null
                ? creditsData.getCast().stream()
                        .map(c -> CastResponse.builder()
                                .personId(c.getId())
                                .name(c.getName())
                                .character(c.getCharacter())
                                .profilePath(c.getProfilePath())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        List<CrewResponse> crew = creditsData.getCrew() != null
                ? creditsData.getCrew().stream()
                        .map(c -> CrewResponse.builder()
                                .personId(c.getId())
                                .name(c.getName())
                                .department(c.getDepartment())
                                .profilePath(c.getProfilePath())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        return CreditsResponse.builder()
                .cast(cast)
                .crew(crew)
                .build();
    }

    private List<ReviewResponse> buildReviewResponses(List<Review> reviews, User currentUser) {
        return reviews.stream()
                .map(review -> {
                    boolean isLiked = currentUser != null && review.isLikedBy(currentUser);

                    // TODO: ReviewComment 조회 추가 필요

                    return ReviewResponse.builder()
                            .id(review.getId())
                            .content(review.getContent())
                            .rating(review.getRating())
                            .createdAt(review.getCreateReview())
                            .updatedAt(review.getUpdateReview())
                            .userId(review.getUser().getId())
                            .username(review.getUser().getUsername())
                            .nickname(review.getUser().getNickname())
                            .movieId(review.getMovie().getId())
                            .movieTitle(review.getMovie().getTitle())
                            .emotionId(review.getEmotion() != null ? review.getEmotion().getId() : null)
                            .emotionName(review.getEmotion() != null ? review.getEmotion().getName() : null)
                            .likesCount(review.getLikes().size())
                            .isLiked(isLiked)
                            .comments(List.of()) // TODO: 댓글 조회 구현 필요
                            .build();
                })
                .toList();
    }
}
