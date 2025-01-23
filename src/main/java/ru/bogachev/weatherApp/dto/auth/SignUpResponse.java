package ru.bogachev.weatherApp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на запрос регистрации")
public record SignUpResponse(

        @Schema(description = "Статус операции, например: success или failure", example = "success")
        String status,

        @Schema(description = "Сообщение с дополнительной информацией о результате операции", example = "Account created successfully")
        String message) {
}
