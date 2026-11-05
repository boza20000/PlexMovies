package com.example.movies.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OmdbResponse(
        @JsonProperty("Title") String title,
        @JsonProperty("Plot") String description,
        @JsonProperty("Metascore") String rating,
        @JsonProperty("Released") String releaseDate,
        @JsonProperty("Director") String directors,
        @JsonProperty("Writer") String writers,
        @JsonProperty("Actors") String stars,
        @JsonProperty("Runtime") String duration,
        @JsonProperty("imdbID") String imdbid,
        @JsonProperty("Year") String year,
        @JsonProperty("Genre") String genre,
        @JsonProperty("Language") String subtitles,
        @JsonProperty("Rated") String rated,
        @JsonProperty("Country") String country,
        @JsonProperty("Awards") String awards,
        @JsonProperty("Poster") String poster,
        @JsonProperty("Ratings") List<RatingDto> ratings,
        @JsonProperty("imdbRating") String imdbRating,
        @JsonProperty("imdbVotes") String votes,
        @JsonProperty("DVD") String dvd,
        @JsonProperty("BoxOffice") String boxOffice,
        @JsonProperty("Production") String production,
        @JsonProperty("Website") String website,
        @JsonProperty("Response") String response

) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RatingDto(
            @JsonProperty("Source") String source,
            @JsonProperty("Value") String value
    ) {
    }
}
