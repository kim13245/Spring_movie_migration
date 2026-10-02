package com.example.movieproject.domain.movie.repository.core;

import com.example.movieproject.domain.movie.entity.core.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    List<Emotion> findByNameContaining(String name);
}