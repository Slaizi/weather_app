package ru.bogachev.weatherApp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на обновление токенов")
public record RefreshRequest(

        @Schema(description = "Токен обновления")
        @NotNull(message = "Токен обновления не может быть пустым")
        String refreshToken
) {
}
