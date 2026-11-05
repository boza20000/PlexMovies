package com.example.movies.repository;

import com.example.movies.model.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    Page<MovieEntity> findMovieEntitiesByTitle(String title, Pageable pageable);

    Page<MovieEntity> findMovieEntitiesByImdbid(String imdbid, Pageable pageable);

    @Query("Select m.imdbid from MediaItemEntity m where type(m) = MovieEntity ")
    List<String> findAllImdbs();
}
