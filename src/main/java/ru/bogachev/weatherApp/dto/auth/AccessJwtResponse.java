package ru.bogachev.weatherApp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ, содержащий токен доступа, используемый для авторизации пользователя.")
public record AccessJwtResponse(

        @Schema(description = "Токен доступа, предоставляющий доступ к защищённым ресурсам системы. Имеет ограниченный срок действия.")
        String accessToken
) {
}
