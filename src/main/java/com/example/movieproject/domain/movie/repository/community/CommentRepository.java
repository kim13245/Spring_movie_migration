package com.example.movieproject.domain.movie.repository.community;

import com.example.movieproject.domain.movie.entity.community.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}