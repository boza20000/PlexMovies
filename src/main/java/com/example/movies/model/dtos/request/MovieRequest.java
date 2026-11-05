package com.example.movies.model.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class MovieRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    private Integer rating;
    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;
    @NotNull(message = "Directors are required")
    private List<String> directors;
    @NotNull
    private List<String> writers;
    @NotNull(message = "Movie stars are required")
    private List<String> stars;
    @NotNull
    private String duration;
    @NotNull
    private String imdbid;
    @NotNull
    private String year;
    @NotNull
    private List<String> genre;
    @NotNull
    private List<String> audio;
    @NotNull
    private List<String> subtitles;

    private List<String> awards;


}