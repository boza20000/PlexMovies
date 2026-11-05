package com.example.movies.exceptions;

import java.util.Arrays;

public class MissingEntityInformationException extends RuntimeException {
    public MissingEntityInformationException(String entity, String... misingElements) {
        super("Missing " + entity + " information for: " + Arrays.toString(misingElements));
    }
}
