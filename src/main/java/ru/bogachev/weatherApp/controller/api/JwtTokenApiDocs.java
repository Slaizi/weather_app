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
import ru.bogachev.weatherApp.dto.auth.AccessJwtResponse;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.RefreshJwtRequest;
import ru.bogachev.weatherApp.dto.exception.ErrorMessage;
import ru.bogachev.weatherApp.dto.exception.ExceptionBody;

@Tag(name = "Токены", description = "Jwt API")
@RequestMapping("/api/v1/token")
public interface JwtTokenApiDocs {

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
    @PostMapping(value = "/access",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccessJwtResponse> refreshAccessToken(
            @RequestBody @Valid RefreshJwtRequest request);

    @Operation(summary = "Обновление токенов доступа.")
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
            @RequestBody @Valid RefreshJwtRequest request);
}
