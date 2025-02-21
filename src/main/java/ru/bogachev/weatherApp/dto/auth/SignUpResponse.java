package ru.bogachev.weatherApp.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.bogachev.weatherApp.model.user.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Ответ на запрос регистрации")
public record SignUpResponse(

        @Schema(description = "Идентификатор пользователя.", example = "1")
        Long id,

        @Schema(description = "Адрес электронной почты для входа.", example = "user@example.com")
        String email,

        @Schema(description = "Роли пользователя.", example = "[\"ROLE_USER\"]")
        Set<Role> role,

        @Schema(description = "Время регистрации пользователя.", example = "19-02-2025 15:18")
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
        LocalDateTime localDateTime
) {
}
