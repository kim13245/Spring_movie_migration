package com.example.movieproject.domain.movie.repository.core;

import com.example.movieproject.domain.movie.entity.core.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {}