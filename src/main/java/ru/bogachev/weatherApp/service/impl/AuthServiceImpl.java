package ru.bogachev.weatherApp.service.impl;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.service.AuthService;
import ru.bogachev.weatherApp.service.UserService;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public SignUpResponse singUp(@NonNull final SignUpRequest request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(Role.ROLE_USER))
                .registerDate(LocalDateTime.now())
                .build();

        userService.create(user);
        return new SignUpResponse("SUCCESS",
                "Пользователь успешно зарегистрирован");
    }

    @Override
    public SignInResponse signIn(@NonNull final SignInRequest request) {
        User user = userService.getByEmail(request.email());

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return new SignInResponse(accessToken, refreshToken);
    }

    @Override
    public SignInResponse refreshTokens(@NonNull final RefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Токен обновления не валиден");
        }

        User user = getUserFromRefreshToken(refreshToken);
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        return new SignInResponse(accessToken, newRefreshToken);
    }

    private User getUserFromRefreshToken(final String refreshToken) {
        Claims refreshClaims = jwtTokenProvider.getRefreshClaims(refreshToken);
        String email = refreshClaims.getSubject();
        return userService.getByEmail(email);
    }
}
