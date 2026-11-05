package com.example.movies.model.dtos.response;

import com.example.movies.model.AwardEntity;
import com.example.movies.model.GenreEntity;
import com.example.movies.model.LanguageEntity;
import com.example.movies.model.PersonEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class MovieResponse {
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
}
