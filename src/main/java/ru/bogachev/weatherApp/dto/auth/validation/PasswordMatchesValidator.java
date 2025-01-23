package ru.bogachev.weatherApp.dto.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NonNull;
import ru.bogachev.weatherApp.dto.auth.SignUpRequest;


public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, SignUpRequest> {

    @Override
    public boolean isValid(
            @NonNull final SignUpRequest signUpRequest,
            @NonNull final ConstraintValidatorContext context) {
        boolean matches = signUpRequest.password().equals(
                signUpRequest.passwordConformation()
        );
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Пароли не совпадают")
                    .addPropertyNode("passwordConformation")
                    .addConstraintViolation();
        }
        return matches;
    }
}
