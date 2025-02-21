package ru.bogachev.weatherApp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на обновление токена/токенов")
public record RefreshJwtRequest(

        @Schema(description = "Токен обновления")
        @NotBlank(message = "Токен обновления не может быть пустым")
        String refreshToken
) {
}
