package ru.bogachev.weatherApp.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.auth.AccessJwtResponse;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.RefreshJwtRequest;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.service.JwtTokenService;
import ru.bogachev.weatherApp.service.TokenStorageService;
import ru.bogachev.weatherApp.support.helper.JwtHelper;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtHelper jwtHelper;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStorageService tokenStorage;

    @Override
    public AccessJwtResponse generateAccessToken(
            final @NotNull RefreshJwtRequest request) {
        String refToken = request.refreshToken();
        User user = jwtHelper.validateAndGetUserFromToken(refToken);

        final String newAccessToken = jwtTokenProvider
                .generateAccessToken(user);

        return new AccessJwtResponse(newAccessToken);
    }

    @Override
    public JwtResponse generateAccessAndRefreshTokens(
            final @NotNull RefreshJwtRequest request) {
        String refToken = request.refreshToken();
        User user = jwtHelper.validateAndGetUserFromToken(refToken);

        final String accessToken = jwtTokenProvider
                .generateAccessToken(user);
        final String newRefreshToken = jwtTokenProvider
                .generateRefreshToken(user);

        tokenStorage.save(user.getId(), newRefreshToken);
        return new JwtResponse(accessToken, newRefreshToken);
    }
}
