package ru.bogachev.weatherApp.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Объект для представления ошибки в API")
public record ExceptionBody(

        @Schema(description = "Сообщение об ошибке", example = "Некорректные данные")
        String message,

        @Schema(description = "Детализация ошибок по полям ввода",
                example = "{\"email\": \"Некорректный формат email\", \"password\": \"Пароль слишком короткий\"}")
        Map<String, String> errors
) {
}
