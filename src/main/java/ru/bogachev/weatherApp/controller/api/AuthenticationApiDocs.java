package ru.bogachev.weatherApp.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.SignInRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpResponse;
import ru.bogachev.weatherApp.dto.exception.ErrorMessage;
import ru.bogachev.weatherApp.dto.exception.ExceptionBody;

@Tag(name = "Аутентификация", description = "Auth API")
@RequestMapping("/api/v1/auth")
public interface AuthenticationApiDocs {

    @Operation(summary = "Регистрация пользователя.")
    @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно зарегистрирован.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SignUpResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка/ошибки валидации запроса.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionBody.class)
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "Пользователь с таким адресом почты уже существует.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class))
    )
    @PostMapping(value = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SignUpResponse> signUp(
            @RequestBody @Valid SignUpRequest request);

    @Operation(summary = "Авторизация пользователя.")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно авторизирован.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JwtResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка/ошибки валидации запроса.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionBody.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Неавторизованный доступ: неверные учетные данные.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Пользователь не был найден.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class)
            )
    )
    @PostMapping(value = "/sign-in",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<JwtResponse> signIn(
            @RequestBody @Valid SignInRequest request);

}
