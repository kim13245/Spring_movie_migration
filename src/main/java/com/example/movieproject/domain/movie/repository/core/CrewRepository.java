package com.example.movieproject.domain.movie.repository.core;

import com.example.movieproject.domain.movie.entity.core.Crew;
import com.example.movieproject.domain.movie.entity.core.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    List<Crew> findByMovie(Movie movie);
}