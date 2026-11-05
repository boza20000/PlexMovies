package com.example.movies.service.impl;

import com.example.movies.exceptions.*;
import com.example.movies.external_api.OmdbClient;
import com.example.movies.model.*;
import com.example.movies.page_filters.MoviePageProcessor;
import com.example.movies.mapper.MovieMapper;
import com.example.movies.model.dtos.MovieDto;
import com.example.movies.model.dtos.request.MovieRequest;
import com.example.movies.constants.StringBundle.DefaultConstants;
import com.example.movies.repository.*;
import com.example.movies.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final AwardRepository awardRepository;
    private final GenreRepository genreRepository;
    private final PersonRepository personRepository;
    private final LanguageRepository languageRepository;
    private final MovieMapper mapper;
    private final MoviePageProcessor pageProcessor;
    private final OmdbClient omdbClient;
    private final GenreService genreService;
    private final LanguageService languageService;
    private final PersonService personService;
    private final AwardService awardService;

    @Override
    public Page<MovieDto> getAllMoviesForPage(int page, int size, String filter, String sort) {
        List<MovieEntity> entities = movieRepository.findAll();
        List<MovieDto> dtos = entities.stream().map(mapper::toDto).toList();
        return pageProcessor.process(dtos, page, size, filter, sort);
    }

    @Override
    public MovieDto enrich(Long id) throws ParseException {
        MovieDto movieDto = getMovieById(id);
        OmdbResponse omdbResponse = omdbClient.fetchByTitleOrImdbId(Optional.of(movieDto.getTitle()), Optional.of(movieDto.getImdbid()));
        mapper.patch(omdbResponse, movieDto);
        MovieEntity enrichedEntity = mapper.toEntity(movieDto);
        Optional<MovieEntity> responseEntity = Optional.of(movieRepository.save(enrichedEntity));
        MovieDto dto = mapper.toDto(responseEntity.get());
        return dto;
    }

    @Override
    public MovieDto getMovieById(long id) {
        Optional<MovieEntity> entity = movieRepository.findById(id);
        return mapper.toDto(entity.orElseThrow(()
                -> new EntityNotFoundException(DefaultConstants.MOVIE_ENTITY)));
    }

    @Override
    public MovieDto createMovie(MovieRequest request) {
        MovieDto dto = mapper.toDto(request);

        if (isDuplicate(dto)) {
            throw new DuplicateEntityException(DefaultConstants.MOVIE_ENTITY);
        }

        MovieEntity entity = mapper.toEntity(dto);
        attachAllEntitiesId(entity);
        MovieEntity response = movieRepository.save(entity);

        return mapper.toDto(response);
    }

    @Override
    public void createMovie(MovieEntity entity) {
        attachAllEntitiesId(entity);
        movieRepository.save(entity);
    }

    private void attachAllEntitiesId(MovieEntity entity) {
        attachGenreIds(entity);
        attachAwardIds(entity);
        attachLanguageIds(entity);
        attachPersonIds(entity);
    }

    private void attachGenreIds(MovieEntity entity) {
        if (entity.getGenres() != null) {
            Set<GenreEntity> genreEntities = entity.getGenres().stream()
                    .map(g -> genreService.createGenre(g.getGenre()))
                    .collect(Collectors.toSet());
            entity.setGenres(genreEntities);
        }
    }

    private void attachLanguageIds(MovieEntity entity) {
        if (entity.getLanguages() != null) {
            Set<LanguageEntity> languageEntities = entity.getLanguages().stream()
                    .map(l -> languageService.createLanguage(l.getLanguage(), l.getStreamType()))
                    .collect(Collectors.toSet());
            entity.setLanguages(languageEntities);
        }
    }

    private void attachPersonIds(MovieEntity entity) {
        if (entity.getPeople() != null) {
            Set<PersonEntity> personEntities = entity.getPeople().stream()
                    .map(p -> personService.createPerson(p.getName(), p.getRole()))
                    .collect(Collectors.toSet());
            entity.setPeople(personEntities);
        }
    }

    private void attachAwardIds(MovieEntity entity) {
        if (entity.getAwards() != null) {
            Set<AwardEntity> awardEntities = entity.getAwards().stream()
                    .map(a -> awardService.createAward(a.getAward()))
                    .collect(Collectors.toSet());
            entity.setAwards(awardEntities);
        }
    }

    private boolean isDuplicate(MovieDto dto) {
        return movieRepository.findAll().stream()
                .map(mapper::toDto)
                .anyMatch(curMovie -> curMovie.equals(dto));
    }

    @Override
    public MovieDto updatePartialMovie(Long id, MoviePartialUpdateRequestDto request) {
        MovieDto toPatch = getMovieById(id);
        MovieDto dto = mapper.toDto(request, toPatch);
        Optional<MovieEntity> response = Optional.of(movieRepository.save(mapper.toEntity(dto)));
        return mapper.toDto(response.get());
    }

    @Override
    public MovieDto updateMovie(Long id, MovieRequest request) {
        getMovieById(id);
        MovieDto dto = mapper.toDto(request);
        Optional<MovieEntity> response = Optional.of(movieRepository.save(mapper.toEntity(dto)));
        return mapper.toDto(response.get());
    }

    @Override
    public MovieDto deleteMovie(Long id) {
        MovieDto dto = getMovieById(id);
        movieRepository.deleteById(id);
        return dto;

    }

}
