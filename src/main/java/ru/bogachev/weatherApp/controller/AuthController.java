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
import ru.bogachev.weatherApp.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Аутентификация", description = "Auth API")
public class AuthController {

    private final AuthenticationService authenticationService;

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
        SignUpResponse response = authenticationService.singUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно авторизирован",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JwtResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Неправильный ввод учетных данных. "
                          + "Например, если адрес электронной почты "
                          + "пользователя не соответствует стандарту. "
                          + "Введённый пароль пустой или длина меньше 5.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionBody.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Неавторизованный доступ: неверные учетные данные.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionBody.class))
    )
    @PostMapping(value = "/sign-in",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtResponse> signIn(
            @RequestBody
            @Validated final SignInRequest request) {
        JwtResponse response = authenticationService.signIn(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновление токена доступа")
    @ApiResponse(
            responseCode = "200",
            description = "Токен доступа успешно обновлен",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JwtResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Токен обновления невалиден или пустой. "
                          + "Например, если токен истек по времени или"
                          + "был отправлен поврежденный или неверный токен.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseBody.class))
    )
    @PostMapping(value = "/token",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtResponse> refreshAccessToken(
            @RequestBody
            @Validated final RefreshJwtRequest request) {
        JwtResponse response = authenticationService.getAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновление токенов доступа")
    @ApiResponse(
            responseCode = "200",
            description = "Токены успешно обновлены",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JwtResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Токен обновления невалиден или пустой. "
                          + "Например, если токен истек по времени или "
                          + "был отправлен поврежденный или неверный токен.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseBody.class))
    )
    @PostMapping(value = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtResponse> refreshTokens(
            @RequestBody
            @Validated final RefreshJwtRequest request) {
        JwtResponse response = authenticationService.refresh(request);
        return ResponseEntity.ok(response);
    }

}
