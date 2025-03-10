package ru.bogachev.weatherApp.service.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.auth.AccessJwtResponse;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.RefreshJwtRequest;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.service.JwtTokenService;
import ru.bogachev.weatherApp.service.TokenStorageService;
import ru.bogachev.weatherApp.service.UserService;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TokenStorageService tokenStorage;

    @Override
    public AccessJwtResponse generateAccessToken(
            final @NotNull RefreshJwtRequest request) {

        String refToken = request.refreshToken();

        if (jwtTokenProvider.validateRefreshToken(refToken)) {
            User user = getUserFromRefreshToken(refToken);
            String currentRefToken = tokenStorage.get(user.getId());

            if (Strings.isNotBlank(currentRefToken)
                && currentRefToken.equals(refToken)) {
                final String newAccessToken = jwtTokenProvider
                        .generateAccessToken(user);
                return new AccessJwtResponse(newAccessToken);
            }
        }
        throw new InvalidTokenException("Токен обновления не валиден.");
    }

    @Override
    public JwtResponse generateAccessAndRefreshTokens(
            final @NotNull RefreshJwtRequest request) {

        String refToken = request.refreshToken();

        if (jwtTokenProvider.validateRefreshToken(refToken)) {
            User user = getUserFromRefreshToken(refToken);
            String currentRefToken = tokenStorage.get(user.getId());

            if (Strings.isNotBlank(currentRefToken)
                && currentRefToken.equals(refToken)) {
                final String newAccessToken = jwtTokenProvider
                        .generateAccessToken(user);
                final String newRefreshToken = jwtTokenProvider
                        .generateRefreshToken(user);

                tokenStorage.save(user.getId(), newRefreshToken);
                return new JwtResponse(newAccessToken, newRefreshToken);
            }
        }
        throw new InvalidTokenException("Токен обновления не валиден.");
    }

    private User getUserFromRefreshToken(final String refToken) {
        Claims claims = jwtTokenProvider.getRefreshClaims(refToken);
        String email = claims.getSubject();
        return userService.getByEmail(email);
    }
}
