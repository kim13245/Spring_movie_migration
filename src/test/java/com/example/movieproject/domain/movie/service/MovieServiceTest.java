package com.example.movieproject.domain.movie.service;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import com.example.movieproject.domain.movie.dto.MovieListResponse;
import com.example.movieproject.domain.movie.entity.core.Movie;
import com.example.movieproject.domain.movie.repository.core.GenreRepository;
import com.example.movieproject.domain.movie.repository.core.MovieRepository;
import com.example.movieproject.domain.movie.repository.core.PersonRepository;
import com.example.movieproject.domain.movie.repository.review.ReviewRepository;
import com.example.movieproject.external.tmdb.TmdbClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @InjectMocks
    private MovieServiceImpl movieService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TmdbClient tmdbClient;

    @Test
    @DisplayName("전체 영화 목록 조회 성공")
    void getAllMovies_Success() {
        // given
        Movie movie1 = Movie.builder()
                .id(1)
                .title("Movie 1")
                .posterPath("/poster1.jpg")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .voteAverage(8.5)
                .popularity(100.0)
                .build();

        Movie movie2 = Movie.builder()
                .id(2)
                .title("Movie 2")
                .posterPath("/poster2.jpg")
                .releaseDate(LocalDate.of(2024, 2, 1))
                .voteAverage(7.5)
                .popularity(90.0)
                .build();

        given(movieRepository.findAll()).willReturn(List.of(movie1, movie2));

        // when
        List<MovieListResponse> responses = movieService.getAllMovies();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).title()).isEqualTo("Movie 1");
        assertThat(responses.get(1).title()).isEqualTo("Movie 2");
    }

    @Test
    @DisplayName("감정별 영화 목록 조회 성공")
    void getMoviesByEmotion_Success() {
        // given
        String emotionName = "기쁨";

        Movie movie1 = Movie.builder()
                .id(1)
                .title("Happy Movie")
                .posterPath("/poster1.jpg")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .voteAverage(8.5)
                .popularity(100.0)
                .build();

        given(genreRepository.findMoviesByEmotionName(emotionName)).willReturn(List.of(movie1));

        // when
        List<MovieListResponse> responses = movieService.getMoviesByEmotion(emotionName);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).title()).isEqualTo("Happy Movie");
    }
}
