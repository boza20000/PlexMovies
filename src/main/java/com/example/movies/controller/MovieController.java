package com.example.movies.controller;

import com.example.movies.config.SecurityRole;
import com.example.movies.mapper.MovieMapper;
import com.example.movies.model.MoviePartialUpdateRequestDto;
import com.example.movies.model.dtos.MovieDto;
import com.example.movies.model.dtos.request.MovieRequest;
import com.example.movies.model.dtos.response.MovieResponse;
import com.example.movies.service.MigrationService;
import com.example.movies.service.MovieService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.ParseException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {

    private final MovieService service;
    private final MovieMapper mapper;
    private final MigrationService migrationService;

    @GetMapping(value = "/{id}")
    @PreAuthorize(SecurityRole.Constants.USER)
    public ResponseEntity<MovieResponse> getMovieById(@PositiveOrZero @PathVariable Long id) {
        MovieDto dto = service.getMovieById(id);
        MovieResponse movie = mapper.toResponse(dto);
        return ResponseEntity.ok(movie);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize(SecurityRole.Constants.ADMIN)
    public ResponseEntity<MovieResponse> updatePartialMovie(@PositiveOrZero @PathVariable Long id, @RequestBody MoviePartialUpdateRequestDto request) {
        MovieDto dto = service.updatePartialMovie(id, request);
        MovieResponse response = mapper.toResponse(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize(SecurityRole.Constants.ADMIN)
    public ResponseEntity<MovieResponse> updateMovie(@PositiveOrZero @PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        MovieDto dto = service.updateMovie(id, request);
        MovieResponse response = mapper.toResponse(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize(SecurityRole.Constants.ADMIN)
    public ResponseEntity<MovieResponse> deleteMovie(@PositiveOrZero @PathVariable Long id) {
        MovieDto dto = service.deleteMovie(id);
        MovieResponse response = mapper.toResponse(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PreAuthorize(SecurityRole.Constants.ADMIN)
    @PostMapping()
    public ResponseEntity<MovieResponse> addMovie(@Valid @RequestBody MovieRequest request) {
        MovieDto dto = service.createMovie(request);
        MovieResponse response = mapper.toResponse(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping()
    @PreAuthorize(SecurityRole.Constants.USER)
    public ResponseEntity<Page<MovieResponse>> getMovies(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero int page,
                                                         @RequestParam(required = false, defaultValue = "10") @Min(5) int size,
                                                         @RequestParam(required = false) String filter,
                                                         @RequestParam(required = false, defaultValue = "id,ASC") String sort) {

        Page<MovieDto> dtoResult = service.getAllMoviesForPage(page, size, filter, sort);
        Page<MovieResponse> responsePage = mapper.toPageResponse(dtoResult);
        return ResponseEntity.ok(responsePage);
    }

    @PreAuthorize(SecurityRole.Constants.ADMIN)
    @PutMapping(value = "/{id}/enrich")
    public ResponseEntity<MovieResponse> enrich(@PositiveOrZero @PathVariable Long id) throws ParseException {
        MovieDto dto = service.enrich(id);
        MovieResponse response = mapper.toResponse(dto);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(SecurityRole.Constants.ADMIN)
    @GetMapping(value = "/migrate/movies")
    public String migrateSqlite() {
        return migrationService.migrateSqliteToPostgresMovies();
    }


}
