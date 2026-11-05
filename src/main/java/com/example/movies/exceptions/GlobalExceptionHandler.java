package com.example.movies.exceptions;

import com.example.movies.constants.StringBundle.ExceptionStrings;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.text.ParseException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ProblemDetail handleUnauthorized(HttpClientErrorException.Unauthorized e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                e.getMessage()
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                e.getMessage()
        );
    }

    @ExceptionHandler(MissingEntityInformationException.class)
    public ProblemDetail handleMissingEntityInformationException(MissingEntityInformationException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("JSON Parse error");
        problem.setDetail(e.getMessage());
        return problem;
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ProblemDetail handleDuplicateEntityException(DuplicateEntityException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle(ExceptionStrings.DUPLICATED_ENTRY_TITLE);
        problem.setDetail(e.getMessage());

        return problem;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle(ExceptionStrings.NOT_FOUND_TITLE);
        problem.setDetail(e.getMessage());

        return problem;
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleResourceAccessException(ResourceAccessException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle(ExceptionStrings.TIME_OUT);
        problem.setDetail(e.getMessage());

        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(ExceptionStrings.ARGUMENTS_NOT_VALID_TITLE);
        problem.setDetail(e.getMessage());

        return problem;
    }

    @ExceptionHandler(ExternalApiException.class)
    public ProblemDetail handleExternalApiException(ExternalApiException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(ExceptionStrings.API_EXCEPTION_TITLE);
        problem.setDetail(e.getMessage());

        return problem;
    }

    @ExceptionHandler(ParseException.class)
    public ProblemDetail handleParseException(ParseException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(ExceptionStrings.API_EXCEPTION_TITLE);
        problem.setDetail(e.getMessage());

        return problem;
    }

    //when creating resource with required fields and null is provided instead
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle(ExceptionStrings.ARGUMENTS_NOT_VALID_TITLE);

        String cleanDetail = "Invalid arguments: " + e.getBindingResult().getFieldErrors().stream()
                .map(f -> "Value '" + f.getRejectedValue() + "' is not valid for the field '" + f.getField())
                .collect(Collectors.joining("; "));

        problem.setDetail(cleanDetail);

        return problem;
    }

    //when number in the uri is out of expected bounds
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle(ExceptionStrings.CONSTRAINT_VIOLATION_TITLE);

        String cleanDetail = "Constraint violation: " + e.getConstraintViolations().stream()
                .map(v -> "The value: " + v.getInvalidValue() + " is not in the constraints " + v.getMessage())
                .collect(Collectors.joining("; "));
        problem.setDetail(cleanDetail);

        return problem;
    }

    //when type provided does not match expected one in the uri
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setDetail("Type is mismatched");
        problem.setTitle(ExceptionStrings.TYPE_VIOLATION_TITLE);

        String required = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown type";
        String cleanDetail = String.format("Required type is %s but is provided value %s of type %s", required, e.getValue(), e.getValue().getClass().getSimpleName());
        problem.setDetail(cleanDetail);

        return problem;
    }

}
