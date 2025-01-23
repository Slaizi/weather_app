package ru.bogachev.weatherApp.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bogachev.weatherApp.dto.exception.ExceptionBody;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.exception.UserNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionBody> handleIllegalArgument(
            final IllegalArgumentException e
    ) {
        return returnDefaultResponseBadRequest(e);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleUsernameNotFound(
            final UserNotFoundException e
    ) {
        return returnDefaultResponseBadRequest(e);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionBody> handleInvalidToken(
            final InvalidTokenException e
    ) {
        return returnDefaultResponseBadRequest(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionBody> handleMethodArgumentNotValid(
            final @NotNull MethodArgumentNotValidException e
    ) {
        List<FieldError> fieldError = e.getBindingResult().getFieldErrors();
        Map<String, String> errors = fieldError.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        Objects.requireNonNull(FieldError::getDefaultMessage)
                ));
        ExceptionBody response = new ExceptionBody(
                "Некорректные данные", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionBody> handleConstraintViolation(
            final @NotNull ConstraintViolationException e
    ) {
        Map<String, String> errors = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        ExceptionBody response = new ExceptionBody(
                "Некорректные данные", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private @NotNull ResponseEntity<ExceptionBody>
    returnDefaultResponseBadRequest(
            final @NotNull RuntimeException exception) {
        ExceptionBody response = new ExceptionBody(
                exception.getMessage(),
                Map.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}
