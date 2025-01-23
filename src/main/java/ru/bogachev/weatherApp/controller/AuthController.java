package ru.bogachev.weatherApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.dto.exception.ExceptionBody;
import ru.bogachev.weatherApp.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Аутентификация", description = "Auth API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно зарегистрирован",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SignUpResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации запроса или некорректные данные. "
                          + "Например, если пароли не совпадают "
                          + "или введённый email некорректен.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionBody.class))
    )
    @PostMapping(value = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignUpResponse> signUp(
            @RequestBody
            @Validated final SignUpRequest request) {
        SignUpResponse response = authService.singUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно авторизирован",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SignInResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Пользователь не был найден или "
                          + "неверный пароль. Например, если"
                          + "пользователь не был зарегистрирован"
                          + "или введённый неверный пароль.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionBody.class))
    )
    @PostMapping(value = "/sign-in",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignInResponse> signIn(
            @RequestBody
            @Validated final SignInRequest request) {
        SignInResponse response = authService.signIn(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновление токенов доступа")
    @ApiResponse(
            responseCode = "200",
            description = "Токены успешно обновлены",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SignInResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Токен обновления невалиден. "
                          + "Например, если токен истек по времени или"
                          + "был отправлен поврежденный или неверный токен.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseBody.class))
    )
    @PostMapping(value = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignInResponse> refreshTokens(
            @RequestBody
            @Validated final RefreshRequest request) {
        SignInResponse response = authService.refreshTokens(request);
        return ResponseEntity.ok(response);
    }

}
