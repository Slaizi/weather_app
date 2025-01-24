package ru.bogachev.weatherApp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ, содержащий токены доступа и обновления, используемые для аутентификации и авторизации пользователя.")
public record JwtResponse(

        @Schema(description = "Токен доступа, предоставляющий доступ к защищённым ресурсам системы. Имеет ограниченный срок действия.")
        String accessToken,

        @Schema(description = "Токен обновления, используемый для получения нового токена доступа по истечении срока действия текущего токена.")
        String refreshToken
) {
}
