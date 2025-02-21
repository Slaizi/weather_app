package ru.bogachev.weatherApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.dto.exception.ErrorMessage;
import ru.bogachev.weatherApp.dto.exception.ExceptionBody;

@Tag(name = "Аутентификация", description = "Auth API")
public interface AuthenticationApi {

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
    ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request);

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
    ResponseEntity<JwtResponse> signIn(@RequestBody SignInRequest request);

    @Operation(summary = "Обновление токена доступа.")
    @ApiResponse(
            responseCode = "200",
            description = "Токен доступа успешно обновлен.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccessJwtResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка/ошибки валидации запроса.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionBody.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Токен обновления невалиден.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class))
    )
    @PostMapping(value = "/token",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccessJwtResponse> refreshAccessToken(
            @RequestBody RefreshJwtRequest request);

    @Operation(summary = "Обновление токенов доступа")
    @ApiResponse(
            responseCode = "200",
            description = "Токены успешно обновлены.",
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
            description = "Токен обновления невалиден.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class))
    )
    @PostMapping(value = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<JwtResponse> refreshTokens(
            @RequestBody RefreshJwtRequest request);

}
