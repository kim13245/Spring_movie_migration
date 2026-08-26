package com.example.movieproject.domain.movie.repository.playlist;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.playlist.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUser(User user);
}