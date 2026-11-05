package com.example.movies.repository;

import com.example.movies.model.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    Optional<GenreEntity> findGenreEntitiesByGenreIgnoreCase(String genre);

}
