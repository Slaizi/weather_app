package ru.bogachev.weatherApp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на аутентификацию")
public record SignInRequest(

        @Schema(description = "Адрес электронной почты", example = "user@example.com")
        @Size(min = 5, max = 50, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @Email(message = "Адрес электронной почты должен быть в формате user@example.com")
        String email,

        @Schema(description = "Пароль", example = "my_1secret1_password")
        @Size(min = 5, max = 255, message = "Длина пароля должна быть от 5 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустыми")
        String password
) {

}
