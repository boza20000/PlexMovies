package com.example.movies.service;

import com.example.movies.exceptions.DuplicateEntityException;
import com.example.movies.exceptions.EntityNotFoundException;
import com.example.movies.external_api.OmdbClient;
import com.example.movies.mapper.MovieMapper;
import com.example.movies.model.MovieEntity;
import com.example.movies.model.MoviePartialUpdateRequestDto;
import com.example.movies.model.OmdbResponse;
import com.example.movies.model.dtos.MovieDto;
import com.example.movies.model.dtos.request.MovieRequest;
import com.example.movies.page_filters.MoviePageProcessor;
import com.example.movies.repository.MovieRepository;
import com.example.movies.service.impl.MovieServiceImpl;
import com.example.movies.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    private MovieEntity movieTestEntity;
    private MovieDto movieTestDto;
    private MovieRequest testRequest;
    private MoviePartialUpdateRequestDto testPartialRequest;

    @Mock
    private MovieRepository repository;

    @Mock
    private MovieMapper mapper;

    @Mock
    private MoviePageProcessor pageProcessor;

    @Mock
    private OmdbClient omdbClient;

    @InjectMocks
    private MovieServiceImpl service;

    @BeforeEach
    void setup() {

        movieTestDto = new MovieDto();
        movieTestEntity = Helper.createMovie1();
        movieTestDto = Helper.mapToDto(movieTestEntity);
        testRequest = Helper.createRequest(movieTestEntity);
        testPartialRequest = Helper.createPartialRequest();

    }

    @Test
    public void givenMovieGetByIdIsCalledWhenMovieIsNotSavedThenExceptionThrown() {

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getMovieById(1));

    }

    @Test
    public void givenMovieGetByIdIsCalledWhenMovieIsSavedInRepositoryThenMovieIsReturned() {

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(movieTestEntity));
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenReturn(movieTestDto);

        MovieDto dto = service.getMovieById(1L);

        assertNotNull(dto);
        assertEquals(movieTestDto, dto);

    }

    @Test
    public void givenValidMovieWhenUserWantToCreateMovieThenMovieIsSaved() {

        Mockito.when(repository.save(any(MovieEntity.class))).thenReturn(movieTestEntity);
        Mockito.when(mapper.toDto(any(MovieRequest.class))).thenReturn(movieTestDto);
        Mockito.when(mapper.toEntity(any(MovieDto.class))).thenReturn(movieTestEntity);
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenReturn(movieTestDto);
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        MovieDto returnDto = service.createMovie(testRequest);

        assertNotNull(returnDto);
        verify(repository).save(any(MovieEntity.class));
    }

    @Test
    public void givenValidMovieWhenUserWantToCreateDuplicatedMovieThenExceptionIsThrown() {

        Mockito.when(mapper.toDto(any(MovieRequest.class))).thenReturn(movieTestDto);
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenReturn(movieTestDto);

        Mockito.when(repository.findAll()).thenReturn(Helper.getMovies());

        assertThrows(DuplicateEntityException.class, () -> service.createMovie(testRequest));

    }

    @Test
    public void givenValidIndexWhenUserWantToDeleteMovieThenMovieIsDeleted() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(movieTestEntity));
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenReturn(movieTestDto);
        service.deleteMovie(1L);
        verify(repository).deleteById(anyLong());

    }

    @Test
    public void givenInValidIndexWhenUserWantToUpdateMovieThenMovieIsNotUpdatedButExceptionThrown() {

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.deleteMovie(500L));
        assertThrows(EntityNotFoundException.class, () -> service.deleteMovie(-500L));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    public void givenInValidIndexWhenUserWantToMakeUpdateOnMovieThenMovieIsNotUpdatedButExceptionThrown() {

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.updateMovie(500L, testRequest));
        assertThrows(EntityNotFoundException.class, () -> service.updateMovie(-500L, testRequest));
        verify(repository, never()).save(any(MovieEntity.class));
    }

    @Test
    public void givenInValidIndexWhenUserWantToMakePartialUpdateOnMovieThenMovieIsNotUpdatedButExceptionThrown() {

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.updatePartialMovie(500L, testPartialRequest));
        assertThrows(EntityNotFoundException.class, () -> service.updatePartialMovie(-500L, testPartialRequest));
        verify(repository, never()).save(any(MovieEntity.class));
    }

    @Test
    public void givenValidIndexAndRequestWhenUserWantToMakeUpdateOnMovieThenMovieIsUpdated() {

        Mockito.when(repository.save(any(MovieEntity.class))).thenReturn(movieTestEntity);
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenReturn(movieTestDto);
        Mockito.when(mapper.toEntity(any(MovieDto.class))).thenReturn(movieTestEntity);
        Mockito.when(mapper.toDto(any(MovieRequest.class))).thenReturn(movieTestDto);
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(movieTestEntity));

        MovieDto returnDto = service.updateMovie(1L, testRequest);

        assertNotNull(returnDto);
        verify(repository).save(any(MovieEntity.class));
    }

    @Test
    public void givenInValidIndexAndPartialRequestWhenUserWantToMakePartialUpdateOnMovieThenMovieIsUpdated() {

        MovieDto oldDto = Helper.mapToDto(Helper.createMovie1());
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(movieTestEntity));
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenReturn(movieTestDto);
        Mockito.when(mapper.toEntity(any(MovieDto.class))).thenReturn(movieTestEntity);
        Mockito.when(mapper.toDto(any(MoviePartialUpdateRequestDto.class), any(MovieDto.class))).thenReturn(Helper.makePartialUpdate(testPartialRequest, movieTestDto));
        Mockito.when(repository.save(any(MovieEntity.class))).thenReturn(movieTestEntity);

        MovieDto newDto = service.updatePartialMovie(1L, testPartialRequest);
        assertNotNull(newDto);
        assertNotEquals(oldDto, newDto);
        assertEquals("Partial Update", newDto.getTitle());
        verify(repository).save(any(MovieEntity.class));
    }

    @Test
    public void givenValidPaginationRequestWhenUserWantToSeePageThenPageIsCreated() {

        List<MovieDto> dtos = Helper.getMovies()
                .stream()
                .map(Helper::mapToDto)
                .toList();
        List<MovieDto> subList = dtos.stream().toList().subList(0, 2);
        Page<MovieDto> page = new PageImpl<>(subList);

        Mockito.when(repository.findAll()).thenReturn(Helper.getMovies());
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenAnswer(i -> Helper.mapToDto(i.getArgument(0)));
        Mockito.when(pageProcessor.process(anyList(), eq(0), eq(5), anyString(), anyString())).thenReturn(page);

        Page<MovieDto> pageResult = service.getAllMoviesForPage(0, 5, "", "id,ASC");

        assertEquals(2, pageResult.getSize());
        verify(pageProcessor).process(anyList(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    public void givenInValidPaginationRequestWhenUserWantToSeePageThenExceptionIsNotThrown() {

        List<MovieDto> dtos = Helper.getMovies()
                .stream()
                .map(Helper::mapToDto)
                .toList();
        List<MovieDto> subList = dtos.stream().toList().subList(0, 2);
        Page<MovieDto> page = new PageImpl<>(subList);

        Mockito.when(repository.findAll()).thenReturn(Helper.getMovies());
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenAnswer(i -> Helper.mapToDto(i.getArgument(0)));
        Mockito.when(pageProcessor.process(anyList(), eq(0), eq(5), anyString(), anyString())).thenReturn(page);

        assertDoesNotThrow(() -> service.getAllMoviesForPage(0, 5, "", "smth,Invalid"), "When invalid argument is passed as sorting parameters but split by comma exception is not thrown default case used");
        verify(pageProcessor).process(anyList(), anyInt(), anyInt(), anyString(), anyString());
    }


    @Test
    public void givenValidIdWhenEnrichIsCalledThenMovieIsEnriched() throws ParseException {
        OmdbResponse testResponse = Helper.testOmdbResponse();
        MovieDto oldInstance = Helper.mapToDto(Helper.createMovie1());

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(movieTestEntity));
        Mockito.when(mapper.toDto(any(MovieEntity.class))).thenReturn(movieTestDto);
        Mockito.when(omdbClient.fetchByTitleOrImdbId(any(), any())).thenReturn(testResponse);
        Mockito.when(mapper.toEntity(any(MovieDto.class))).thenReturn(movieTestEntity);

        Mockito.doAnswer(i -> {
            OmdbResponse response = i.getArgument(0);
            MovieDto dto = i.getArgument(1);
            Helper.makeUpdate(dto, response);
            return null;
        }).when(mapper).patch(any(OmdbResponse.class), any(MovieDto.class));

        MovieDto toBeEnriched = Helper.mapToDto(Helper.createMovie1());
        Mockito.when(repository.save(any(MovieEntity.class))).thenReturn(Helper.makeUpdate(toBeEnriched, testResponse));

        MovieDto enrichedInstance = service.enrich(1L);
        assertNotNull(enrichedInstance);
        assertNotEquals(oldInstance, enrichedInstance);
        verify(repository).save(any(MovieEntity.class));

    }

}
