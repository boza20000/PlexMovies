package com.example.movies.mapper;

import com.example.movies.exceptions.ExternalApiException;
import com.example.movies.model.*;
import com.example.movies.model.dtos.MovieDto;
import com.example.movies.model.dtos.SqliteMovieDto;
import com.example.movies.model.dtos.request.MovieRequest;
import com.example.movies.model.dtos.response.MovieResponse;
import com.example.movies.constants.StringBundle.ExceptionStrings;
import com.example.movies.constants.StringBundle.DefaultConstants;
import org.jspecify.annotations.Nullable;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import tools.jackson.databind.introspect.TypeResolutionContext;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {DefaultConstants.class})
public interface MovieMapper {

    MovieEntity toEntity(MovieDto dto);

    MovieResponse toResponse(MovieDto dto);

    MovieDto toDto(MovieEntity movieEntity);

    default Page<MovieResponse> toPageResponse(Page<MovieDto> dtoResult) {
        return dtoResult.map(this::toResponse);
    }

    @Mapping(target = "description", source = "summary")
    @Mapping(target = "releaseDate", dateFormat = "dd MMM yyyy", source = "releaseDate")
    @Mapping(target = "duration", source = "duration", qualifiedByName = "parseIntToStringChain")
    @Mapping(target = "imdbid", source = "guid", qualifiedByName = "stripImdbId")
    @Nullable MovieEntity toEntity(SqliteMovieDto item);

    @AfterMapping
    default void handleSpecialFields(SqliteMovieDto item, @MappingTarget MovieEntity entity) {
        if (entity == null) {
            return;
        }

        entity.setLanguages(handleSqliteLanguages(item.getSubtitles(), item.getAudio()));
        entity.setGenres(handleSqliteGenres(item.getTagsGenre()));
        entity.setPeople(handleSqlitePeople(item.getTagsDirector(), item.getTagsStar(), item.getTagsWriter()));
    }

    default Set<PersonEntity> handleSqlitePeople(String tagsDirector, String tagsStar, String tagsWriter) {
        Set<PersonEntity> personEntities = new HashSet<>();

        personEntities.addAll(parseDirectors(tagsDirector));
        personEntities.addAll(parseWriters(tagsWriter));
        personEntities.addAll(parseStars(tagsStar));

        return personEntities;
    }

    default Set<PersonEntity> parseStars(String tagsStar) {
        if (tagsStar == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(tagsStar.split("\\|"))
                .map(s -> new PersonEntity(null, s, Role.STAR))
                .collect(Collectors.toSet());
    }

    default Set<PersonEntity> parseWriters(String tagsWriter) {
        if (tagsWriter == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(tagsWriter.split("\\|"))
                .map(s -> new PersonEntity(null, s, Role.WRITER))
                .collect(Collectors.toSet());
    }

    default Set<PersonEntity> parseDirectors(String tagsDirector) {
        if (tagsDirector == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(tagsDirector.split("\\|"))
                .map(s -> new PersonEntity(null, s, Role.DIRECTOR))
                .collect(Collectors.toSet());
    }

    default Set<GenreEntity> handleSqliteGenres(String tagsGenre) {
        Set<GenreEntity> genreEntities = new HashSet<>();
        if (tagsGenre != null) {
            for (String g : tagsGenre.split("\\|")) {
                genreEntities.add(new GenreEntity(null, g));
            }
        }
        return genreEntities;
    }

    default Set<LanguageEntity> handleSqliteLanguages(String subtitles, String audio) {
        Set<LanguageEntity> languageEntities = new HashSet<>();
        if (audio != null) {
            for (String s : handleAudio(audio)) {
                languageEntities.add(new LanguageEntity(null, s, StreamType.AUDIO));
            }
        }
        if (subtitles != null) {
            for (String s : handleSubtitles(subtitles)) {
                languageEntities.add(new LanguageEntity(null, s, StreamType.SUBTITLE));
            }
        }
        return languageEntities;
    }

    default List<String> handleAudio(String audio) {
        return Arrays.stream(audio.trim().split(",")).toList();
    }

    default List<String> handleSubtitles(String subtitles) {
        return Arrays.stream(subtitles.trim().split(",")).toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "people", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "awards", ignore = true)
    MovieDto toDto(MovieRequest request);

    @AfterMapping
    default void handleSpecialFields(MovieRequest request, @MappingTarget MovieDto dto) {
        if (request == null) {
            return;
        }

        dto.setAwards(handleAwardEntities(request.getAwards()));
        dto.setLanguages(handleLanguagesEntities(request.getSubtitles(), request.getAudio()));
        dto.setGenres(handleGenreEntities(request.getGenre()));
        dto.setPeople(handlePeople(request.getDirectors(), request.getStars(), request.getWriters()));
    }


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "description", qualifiedByName = "validate")
    @Mapping(target = "rating", source = "rating", qualifiedByName = "parseIntToStringChain")
    @Mapping(target = "releaseDate", dateFormat = "dd MMM yyyy", qualifiedByName = "validate")
    @Mapping(target = "duration", source = "duration", qualifiedByName = "validate")
    @Mapping(target = "imdbid", source = "imdbid", qualifiedByName = "validate")
    @Mapping(target = "year", source = "year", qualifiedByName = "validate")
    @Mapping(target = "people", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "awards", ignore = true)
    void patch(OmdbResponse omdb, @MappingTarget MovieDto movieDto) throws ParseException;


    @AfterMapping
    default void handleSpecialFields(OmdbResponse omdb, @MappingTarget MovieDto dto) {
        if (omdb == null) {
            return;
        }

        dto.setAwards(handleAwardEntities(splitToList(omdb.awards())));
        dto.setLanguages(handleLanguagesEntities(splitToList(omdb.subtitles()), DefaultConstants.DEFAULT_AUDIO));
        dto.setGenres(handleGenreEntities(splitToList(omdb.genre())));
        dto.setPeople(handlePeople(splitToList(omdb.directors()), splitToList(omdb.stars()), splitToList(omdb.writers())));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "people", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "awards", ignore = true)
    @Mapping(target = "imdbid", source = "imdbId")
    MovieDto toDto(MoviePartialUpdateRequestDto request, @MappingTarget MovieDto toPatch);

    @AfterMapping
    default void handleSpecialFields(MoviePartialUpdateRequestDto request, @MappingTarget MovieDto toPatch) {
        if (request == null) {
            return;
        }

        if (request.getAwards() != null) {
            toPatch.setAwards(handleAwardEntities(request.getAwards()));
        }

        if (request.getGenre() != null) {
            toPatch.setGenres(handleGenreEntities(splitToList(request.getGenre())));
        }

        if (request.getWriters() != null || request.getDirectors() != null || request.getStars() != null) {
            toPatch.setPeople(handlePeople(request.getDirectors(), request.getStars(), request.getWriters()));
        }

        if (request.getAudio() != null || request.getSubtitles() != null) {
            toPatch.setLanguages(handleLanguagesEntities(splitToList(request.getSubtitles()), splitToList(request.getAudio())));
        }
    }

    default Set<PersonEntity> handlePeople(List<String> directors, List<String> stars, List<String> writers) {
        Set<PersonEntity> people = new HashSet<>();
        addPeople(people, directors, Role.DIRECTOR);
        addPeople(people, stars, Role.STAR);
        addPeople(people, writers, Role.WRITER);

        return people;
    }

    default Set<LanguageEntity> handleLanguagesEntities(List<String> sub, List<String> audio) {
        Set<LanguageEntity> languages = new HashSet<>();
        addLanguage(languages, sub, StreamType.SUBTITLE);
        addLanguage(languages, audio, StreamType.AUDIO);

        return languages;
    }

    default void addLanguage(Set<LanguageEntity> languages, List<String> audio, StreamType streamType) {
        if (validateList(audio)) return;
        for (String l : audio) {
            languages.add(new LanguageEntity(null, l.trim(), streamType));
        }
    }

    default Set<GenreEntity> handleGenreEntities(List<String> genre) {
        if (validateList(genre)) return Collections.emptySet();

        Set<GenreEntity> genreEntities = new HashSet<>();
        for (String g : genre) {
            genreEntities.add(new GenreEntity(null, g));
        }
        return genreEntities;
    }

    default Set<AwardEntity> handleAwardEntities(List<String> awards) {
        if (validateList(awards)) return Collections.emptySet();

        Set<AwardEntity> awardEntities = new HashSet<>();
        for (String a : awards) {
            awardEntities.add(new AwardEntity(null, a));
        }
        return awardEntities;
    }

    default void addPeople(Set<PersonEntity> people, List<String> peopleToAdd, Role role) {
        if (validateList(peopleToAdd)) {
            return;
        }

        for (String p : peopleToAdd) {
            people.add(new PersonEntity(null, p.trim(), role));
        }
    }

    @Named("stripImdbId")
    default String stripImdbId(String imdbId) {
        return imdbId.split("://|\\?")[1];
    }

    @Condition
    @Named("validateList")
    default boolean validateList(List<String> list) {
        return list == null || list.isEmpty();
    }

    @Named("splitToList")
    default List<String> splitToList(String input) {
        if (validate(input) == null) {
            return null;
        }
        return Arrays.stream(input.split(", "))
                .map(String::trim).filter(s -> !s.isEmpty())
                .toList();
    }

    @Named("validate")
    default String validate(String input) {
        return (isValid(input)) ? input.trim() : null;
    }

    default boolean isValid(String toCompare) {
        return toCompare != null
                && !toCompare.isBlank()
                && !toCompare.equalsIgnoreCase(DefaultConstants.EMPTY_FIELD);
    }

    @BeforeMapping
    default void validateInput(OmdbResponse omdb) {
        if (omdb == null) {
            return;
        }
        if (omdb.response() == null || omdb.response().equals(DefaultConstants.FALSE)) {
            throw new ExternalApiException(ExceptionStrings.EXTERNAL_API_ERROR_RESPONSE);
        }
    }

    @Named("parseIntToStringChain")
    default Integer parseIntToStringChain(String value) {
        if (value == null || validate(value) == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}

