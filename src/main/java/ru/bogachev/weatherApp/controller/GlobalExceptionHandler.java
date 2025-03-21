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
import ru.bogachev.weatherApp.dto.exception.ErrorMessage;
import ru.bogachev.weatherApp.dto.exception.ExceptionBody;
import ru.bogachev.weatherApp.exception.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            GeoNotFoundException.class
    })
    public ResponseEntity<ErrorMessage> handleNotFoundExceptions(
            final @NotNull RuntimeException e
    ) {
        ErrorMessage response = new ErrorMessage(
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleUserAlreadyExists(
            final @NotNull UserAlreadyExistsException e
    ) {
        ErrorMessage response = new ErrorMessage(
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler({
            GeoRequestException.class,
            WeatherRequestException.class
    })
    public ResponseEntity<ErrorMessage> handleBadRequestExceptions(
            final @NotNull RuntimeException e
    ) {
        ErrorMessage response = new ErrorMessage(
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<ErrorMessage> handleUnauthorizedExceptions(
            final @NotNull RuntimeException e
    ) {
        ErrorMessage response = new ErrorMessage(
                e.getMessage()
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
}
