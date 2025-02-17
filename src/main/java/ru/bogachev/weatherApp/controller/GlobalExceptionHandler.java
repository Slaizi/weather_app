package ru.bogachev.weatherApp.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bogachev.weatherApp.dto.exception.ExceptionBody;
import ru.bogachev.weatherApp.exception.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleUserNotFound(
            final UserNotFoundException e
    ) {
        return returnDefaultResponseBadRequest(e);
    }

    @ExceptionHandler(GeoRequestException.class)
    public ResponseEntity<ExceptionBody> handleGeoRequest(
            final GeoRequestException e
    ) {
        return returnDefaultResponseBadRequest(e);
    }

    @ExceptionHandler(WeatherRequestException.class)
    public ResponseEntity<ExceptionBody> handleWeatherRequest(
            final WeatherRequestException e
    ) {
        return returnDefaultResponseBadRequest(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionBody> handleUnauthorized(
            final @NotNull UnauthorizedException e
    ) {
        ExceptionBody response = new ExceptionBody(
                e.getMessage(), Map.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionBody> handleIllegalArgument(
            final IllegalArgumentException e
    ) {
        return returnDefaultResponseBadRequest(e);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionBody> handleInvalidToken(
            final @NotNull InvalidTokenException e
    ) {
        ExceptionBody response = new ExceptionBody(
                e.getMessage(), Map.of()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionBody> handleMethodArgumentNotValid(
            final @NotNull MethodArgumentNotValidException e
    ) {
        List<FieldError> fieldError = e.getBindingResult().getFieldErrors();
        Map<String, String> errors = fieldError.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        Objects.requireNonNull(FieldError::getDefaultMessage),
                        (existing, replacement) -> String.join(
                                ". ",
                                existing, replacement)
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
