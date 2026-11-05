package com.example.movies.model.dtos;

import com.example.movies.model.AwardEntity;
import com.example.movies.model.GenreEntity;
import com.example.movies.model.LanguageEntity;
import com.example.movies.model.PersonEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode

public class MovieDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String title;
    private String description;
    private Integer rating;
    private LocalDate releaseDate;
    private Set<GenreEntity> genres;
    private Set<PersonEntity> people;
    private String duration;
    private String imdbid;
    private String year;
    private Set<LanguageEntity> languages;
    private Set<AwardEntity> awards;

    public MovieDto() {
        this.genres = new HashSet<>();
        this.people = new HashSet<>();
        this.languages = new HashSet<>();
        this.awards = new HashSet<>();
    }

}
