package com.example.movies.model.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter

public class SqliteMovieDto {
    private String title;

    private String summary;

    private Integer rating;

    private LocalDate releaseDate;

    private String tagsDirector;

    private String tagsWriter;

    private String tagsStar;

    private String duration;

    private String guid;

    private String year;

    private String tagsGenre;

    private String audio;

    private String subtitles;

}
