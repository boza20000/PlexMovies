package com.example.movies.utils;

import com.example.movies.mapper.MovieMapper;
import com.example.movies.model.*;
import com.example.movies.model.dtos.MovieDto;
import com.example.movies.model.dtos.request.MovieRequest;
import lombok.experimental.UtilityClass;
import org.mapstruct.factory.Mappers;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class Helper {

    private static final MovieMapper mapper = Mappers.getMapper(MovieMapper.class);

    public MovieEntity createMovie1() {

        MovieEntity movie = new MovieEntity();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDescription("A thief who steals corporate secrets through the use of dream-sharing technology.");
        movie.setRating(9);
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));
        movie.setImdbid("tt1375666");
        movie.setDuration("148 min");
        movie.setYear("2010");
        movie.setGenres(Set.of(new GenreEntity(1L, "Sci-Fi"), new GenreEntity(2L, "Action")));
        movie.setLanguages(Set.of(new LanguageEntity(1L, "English", StreamType.AUDIO)));
        movie.setAwards(Set.of(new AwardEntity(1L, "Won 4 Oscars")));
        movie.setPeople(Set.of(
                new PersonEntity(1L, "Christopher Nolan", Role.DIRECTOR),
                new PersonEntity(2L, "Leonardo DiCaprio", Role.STAR)
        ));
        return movie;
    }

    private MovieEntity createMovie2() {
        MovieEntity movie = new MovieEntity();
        movie.setId(2L);
        movie.setTitle("The Dark Knight");
        movie.setDescription("When the menace known as the Joker wreaks havoc and chaos on Gotham.");
        movie.setRating(9);
        movie.setReleaseDate(LocalDate.of(2008, 7, 18));
        movie.setImdbid("tt0468569");
        movie.setDuration("152 min");
        movie.setYear("2008");
        movie.setGenres(Set.of(new GenreEntity(2L, "Action"), new GenreEntity(3L, "Crime")));
        movie.setLanguages(Set.of(new LanguageEntity(1L, "English", StreamType.AUDIO)));
        movie.setAwards(Set.of(new AwardEntity(2L, "Won 2 Oscars")));
        movie.setPeople(Set.of(
                new PersonEntity(1L, "Christopher Nolan", Role.DIRECTOR),
                new PersonEntity(3L, "Christian Bale", Role.STAR)
        ));
        return movie;
    }

    public MovieRequest createRequest(MovieEntity entity) {
        MovieRequest request = new MovieRequest();
        request.setWriters(entity.getPeople().stream().filter(p -> p.getRole().equals(Role.WRITER)).map(PersonEntity::getName).toList());
        request.setRating(entity.getRating());
        request.setAudio(entity.getLanguages().stream().filter(l -> l.getStreamType().equals(StreamType.AUDIO)).map(LanguageEntity::getLanguage).toList());
        request.setAwards((entity.getAwards().stream().map(AwardEntity::getAward).toList()));
        request.setDescription(entity.getDescription());
        request.setDirectors(entity.getPeople().stream().filter(p -> p.getRole().equals(Role.DIRECTOR)).map(PersonEntity::getName).toList());
        request.setGenre(entity.getGenres().stream().map(GenreEntity::getGenre).toList());
        request.setDuration(entity.getDuration());
        request.setImdbid(entity.getImdbid());
        request.setStars(entity.getPeople().stream().filter(p -> p.getRole().equals(Role.STAR)).map(PersonEntity::getName).toList());
        request.setYear(entity.getYear());
        request.setTitle(entity.getTitle());
        request.setReleaseDate(entity.getReleaseDate());
        request.setSubtitles(entity.getLanguages().stream().filter(l -> l.getStreamType().equals(StreamType.SUBTITLE)).map(LanguageEntity::getLanguage).toList());
        return request;
    }

    public MovieDto mapToDto(MovieEntity testEntity) {
        return mapper.toDto(testEntity);
    }

    public static List<MovieEntity> getMovies() {
        Map<Long, MovieEntity> movies = new ConcurrentHashMap<>();
        movies.put(1L, createMovie1());
        movies.put(2L, createMovie2());
        return movies.values().stream().toList();
    }


    public static MoviePartialUpdateRequestDto createPartialRequest() {
        MoviePartialUpdateRequestDto partialUpdateRequest = new MoviePartialUpdateRequestDto();
        partialUpdateRequest.setTitle("Partial Update");
        return partialUpdateRequest;
    }

    public static MovieDto makePartialUpdate(MoviePartialUpdateRequestDto testPartialRequest, MovieDto movieTestDto) {
        return mapper.toDto(testPartialRequest, movieTestDto);
    }

    public static OmdbResponse testOmdbResponse() {
        List<OmdbResponse.RatingDto> ratings = List.of(
                new OmdbResponse.RatingDto("Internet Movie Database", "9.1/10"),
                new OmdbResponse.RatingDto("Rotten Tomatoes", "94%"),
                new OmdbResponse.RatingDto("Metacritic", "85/100")
        );

        return new OmdbResponse(
                "The Dark Knight",
                "When a menace known as the Joker wreaks havoc...",
                "85",
                "18 Jul 2008",
                "Christopher Nolan",
                "Jonathan Nolan, Christopher Nolan, David S. Goyer",
                "Christian Bale, Heath Ledger, Aaron Eckhart",
                "152 min",
                "tt0468569",
                "2008",
                "Action, Crime, Drama",
                "English, Mandarin",
                "PG-13",
                "United States, United Kingdom",
                "Won 2 Oscars. 163 wins & 165 nominations total",
                "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_QL75_UX380_CR0,0,380,562_.jpg",
                ratings,
                "9.1",
                "3,161,907",
                "N/A",
                "$534,987,076",
                "N/A",
                "N/A",
                "True"
        );
    }

    public static MovieEntity makeUpdate(MovieDto movieDto, OmdbResponse testResponse) throws ParseException {
        mapper.patch(testResponse, movieDto);
        return mapper.toEntity(movieDto);
    }
}