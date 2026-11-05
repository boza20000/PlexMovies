package com.example.movies.processor;

import com.example.movies.mapper.MovieMapper;
import com.example.movies.model.MovieEntity;
import com.example.movies.model.dtos.SqliteMovieDto;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SqliteMovieProcessor implements ItemProcessor<SqliteMovieDto, MovieEntity> {
    private final MovieMapper mapper;
    private final Set<String> processedImdbIds = ConcurrentHashMap.newKeySet();

    @Override
    public @Nullable MovieEntity process(SqliteMovieDto item) {

        if (item.getGuid() == null || item.getGuid().isEmpty()) {
            return null;
        }
        String[] parts = item.getGuid().split("://|\\?");
        if (parts.length != 3 || parts[1] == null) {
            return null;
        }
        String key = parts[1].trim().toLowerCase();

        if (!processedImdbIds.add(key)) {
            return null;
        }

        return mapper.toEntity(item);
    }

    public void fillExistingMovies(List<String> existingImdbs) {
        existingImdbs.stream()
                .filter(Objects::nonNull)
                .filter(id -> !id.isEmpty())
                .map(id -> id.trim().toLowerCase())
                .forEach(processedImdbIds::add);
    }
}
