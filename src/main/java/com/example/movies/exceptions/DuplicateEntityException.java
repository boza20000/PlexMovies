package com.example.movies.exceptions;

import com.example.movies.constants.StringBundle.ExceptionStrings;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String type) {
        super(type + " " + ExceptionStrings.DUPLICATED_ENTRY_TITLE);
    }
}
