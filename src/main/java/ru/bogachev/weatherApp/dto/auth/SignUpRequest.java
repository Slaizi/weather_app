package ru.bogachev.weatherApp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.bogachev.weatherApp.dto.auth.validation.PasswordMatches;

@PasswordMatches
@Schema(description = "Запрос на регистрацию")
public record SignUpRequest(

        @Schema(description = "Адрес электронной почты. Используется для авторизации",
                example = "user@example.com")
        @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @Email(message = "Адрес электронной почты должен быть в формате user@example.com")
        String email,

        @Schema(description = "Пароль. Рекомендуется использовать комбинацию букв, цифр и специальных символов",
                example = "my_1secret1_password")
        @Size(min = 5, max = 255, message = "Длина пароля должна быть от 5 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустым")
        String password,

        @Schema(description = "Подтверждение пароля. Должно совпадать с введённым паролем",
                example = "my_1secret1_password")
        @Size(min = 5, max = 255, message = "Длина подтверждения пароля должна быть от 5 до 255 символов")
        @NotBlank(message = "Подтверждение пароля не может быть пустым")
        String passwordConformation
) {

}
