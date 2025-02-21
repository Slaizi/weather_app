package ru.bogachev.weatherApp.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Объект для представления ошибки в API")
public record ErrorMessage(

        @Schema(example = "Сообщение об ошибки")
        String message
) {
}
