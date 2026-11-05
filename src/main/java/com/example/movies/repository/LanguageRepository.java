package com.example.movies.repository;

import com.example.movies.model.LanguageEntity;
import com.example.movies.model.StreamType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {

    Optional<LanguageEntity> findLanguageEntitiesByLanguageIgnoreCaseAndStreamType(@NotNull String language, StreamType streamType);

}
