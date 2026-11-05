package com.example.movies.service;

import com.example.movies.model.MovieEntity;
import com.example.movies.model.MoviePartialUpdateRequestDto;
import com.example.movies.model.dtos.MovieDto;
import com.example.movies.model.dtos.request.MovieRequest;
import org.springframework.data.domain.Page;

import java.text.ParseException;

public interface MovieService {
    MovieDto getMovieById(long id);

    MovieDto createMovie(MovieRequest movie);

    void createMovie(MovieEntity movie);

    MovieDto updatePartialMovie(Long id, MoviePartialUpdateRequestDto request);

    MovieDto updateMovie(Long id, MovieRequest request);

    MovieDto deleteMovie(Long id);

    Page<MovieDto> getAllMoviesForPage(int page, int size, String filter, String sort);

    MovieDto enrich(Long id) throws ParseException;
}
