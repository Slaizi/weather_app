package ru.bogachev.weatherApp.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Объект для представления ошибки в API")
public record ExceptionBody(

        @Schema(description = "Сообщение об ошибке")
        String message,

        @Schema(description = "Детализация ошибок по полям ввода",
                example = "{\"field1\": \"Некорректный формат ввода\", \"field2\": \"Поле не может быть пустым\"}")
        Map<String, String> errors
) {
}
