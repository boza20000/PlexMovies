package com.example.movies.service.impl;

import com.example.movies.model.GenreEntity;
import com.example.movies.repository.GenreRepository;
import com.example.movies.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public GenreEntity createGenre(String genreName) {
        Optional<GenreEntity> entity = genreRepository.findGenreEntitiesByGenreIgnoreCase(genreName.trim());

        if (entity.isEmpty()) {
            return genreRepository.save(new GenreEntity(null, genreName.trim()));
        }

        return entity.get();
    }
}
