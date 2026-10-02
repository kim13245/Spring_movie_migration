package com.example.movieproject.domain.movie.repository.core;

import com.example.movieproject.domain.movie.entity.core.Cast;
import com.example.movieproject.domain.movie.entity.core.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CastRepository extends JpaRepository<Cast, Long> {
    List<Cast> findByMovie(Movie movie);
}