package com.example.movieproject.domain.movie.repository.playlist;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.movie.entity.playlist.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUser(User user);

    @Query("SELECT DISTINCT c FROM Collection c LEFT JOIN FETCH c.movies WHERE c.user = :user")
    List<Collection> findByUserWithMovies(@Param("user") User user);
}