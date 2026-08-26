package com.example.movieproject.domain.movie.repository.community;

import com.example.movieproject.domain.movie.entity.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {}