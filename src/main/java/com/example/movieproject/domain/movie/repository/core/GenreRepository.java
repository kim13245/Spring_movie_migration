package com.example.movieproject.domain.movie.repository.core;

import com.example.movieproject.domain.movie.entity.core.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g JOIN g.emotions e WHERE e.name = :emotionName")
    List<com.example.movieproject.domain.movie.entity.core.Movie> findMoviesByEmotionName(@Param("emotionName") String emotionName);
}
