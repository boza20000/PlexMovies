package com.example.movies.constants;

import java.util.List;

public class StringBundle {

    public static class ExceptionStrings {
        public static final String DUPLICATED_ENTRY_TITLE = "already in storage";
        public static final String NOT_FOUND_TITLE = "not found";
        public static final String ARGUMENTS_NOT_VALID_TITLE = "arguments not valid";
        public static final String CONSTRAINT_VIOLATION_TITLE = "constraint violation";
        public static final String TYPE_VIOLATION_TITLE = "type violation";
        public static final String API_EXCEPTION_TITLE = "api problem";
        public static final String TIME_OUT = "timed out";
        public static final String EXTERNAL_API_UNAUTHORIZED = "Invalid or missing API key (Unauthorized)";
        public static final String EXTERNAL_API_FORBIDDEN = "Access forbidden by OMDb API (Forbidden)";
        public static final String EXTERNAL_API_RATE_LIMIT_EXCEEDED = "OMDb API rate limit exceeded (Too Many Requests)";
        public static final String EXTERNAL_API_DEFAULT_400 = "Client error from OMDb with status: ";
        public static final String SORT_INVALID_ARGS = "Invalid string arguments in sort param";
        public static final String EXTERNAL_API_ERROR_RESPONSE = "Api send error response";
        public static final String TITLE_UNAUTHORIZED = "Unauthorized";
        public static final String MESSAGE_UNAUTHORIZED = "Authentication token is missing or invalid.";
    }

    public static class DefaultConstants {
        public static final List<String> DEFAULT_AUDIO = List.of("English", "Mandarin", "German");
        public static final String EMPTY_FIELD = "N/A";
        public static final String MOVIE_ENTITY = "Movie";
        public static final String FALSE = "false";
    }

    public static class SecurityConstants {
        public static final String ROLE_ADMIN = "hasRole('ADMIN')";
        public static final String ROLE_USER = "hasRole('USER')";
    }

    public static class FileConstants {
        public static final String BASE_PATH = "/Users/boristsvetkov/IdeaProjects/Software-Engineering-Academy/participants/boris-tsvetkov/backend/plex/movies/src/main/resources/";

        public static final List<String> sqliteFiles = List.of(
                BASE_PATH + "1.db",
                BASE_PATH + "2.db",
                BASE_PATH + "3.db",
                BASE_PATH + "4.db",
                BASE_PATH + "5.db"
        );

    }

}
