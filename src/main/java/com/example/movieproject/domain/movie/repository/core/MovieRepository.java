package com.example.movieproject.domain.movie.repository.core;

import com.example.movieproject.domain.movie.entity.core.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {}