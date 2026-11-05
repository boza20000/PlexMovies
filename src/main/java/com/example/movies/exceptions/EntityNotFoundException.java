package com.example.movies.exceptions;

import com.example.movies.constants.StringBundle.ExceptionStrings;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String type) {
        super(type + " " + ExceptionStrings.NOT_FOUND_TITLE);
    }
}
