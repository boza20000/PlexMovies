package com.example.movies.page_filters;

import com.example.movies.model.dtos.MovieDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;

import static com.example.movies.constants.StringBundle.ExceptionStrings.SORT_INVALID_ARGS;

@NoArgsConstructor
@Component

public class MovieComparator {
    private final String TITLE = "TITLE";

    public Comparator<MovieDto> buildComparator(String sort) {
        String element;
        try {
            String[] command = sort.split(",");
            element = command[0];
        } catch (Exception e) {
            throw new IllegalArgumentException(SORT_INVALID_ARGS);
        }
        if (element == null || element.isEmpty()) {
            return Comparator.comparing(MovieDto::getId);
        }

        switch (element.toUpperCase()) {
            case TITLE -> {
                return Comparator.comparing(MovieDto::getTitle);
            }
            default -> {
                return Comparator.comparing(MovieDto::getId);
            }
        }
    }
}
