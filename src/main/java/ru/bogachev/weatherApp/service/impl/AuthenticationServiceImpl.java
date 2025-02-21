package ru.bogachev.weatherApp.service.impl;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.exception.UnauthorizedException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.security.JwtUserDetails;
import ru.bogachev.weatherApp.service.AuthenticationService;
import ru.bogachev.weatherApp.service.TokenStorageManagementService;
import ru.bogachev.weatherApp.service.UserManagementService;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapper;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserManagementService userManagementService;
    private final AuthenticationManager authManager;
    private final TokenStorageManagementService tokenStorageManagementService;
    private final UserJwtEntityMapper userJwtEntityMapper;

    @Override
    public SignUpResponse singUp(@NonNull final SignUpRequest request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(Role.ROLE_USER))
                .registerDate(LocalDateTime.now())
                .build();

        User savedUser = userManagementService.create(user);

        return new SignUpResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRoles(),
                savedUser.getRegisterDate()
        );
    }

    @Override
    public JwtResponse signIn(@NonNull final SignInRequest request) {
        try {
            Authentication authenticate = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()
                    )
            );
            JwtUserDetails userDetails =
                    (JwtUserDetails) authenticate.getPrincipal();
            User user = userJwtEntityMapper.toEntity(userDetails);

            final String accessToken = jwtTokenProvider
                    .generateAccessToken(user);
            final String refreshToken = jwtTokenProvider
                    .generateRefreshToken(user);

            tokenStorageManagementService.save(user.getId(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Неверный email или пароль.");
        }
    }

    @Override
    public AccessJwtResponse getAccessToken(
            @NonNull final RefreshJwtRequest request) {
        String refreshToken = request.refreshToken();

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            User user = getUserFromRefreshToken(refreshToken);
            String currentToken = tokenStorageManagementService
                    .get(user.getId());

            if (Strings.isNotBlank(currentToken)
                && currentToken.equals(refreshToken)) {
                final String newAccessToken = jwtTokenProvider
                        .generateAccessToken(user);
                return new AccessJwtResponse(newAccessToken);
            }
        }
        throw new InvalidTokenException("Токен обновления не валиден.");
    }

    @Override
    public JwtResponse getRefreshTokens(
            @NonNull final RefreshJwtRequest request) {
        String refreshToken = request.refreshToken();

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            User user = getUserFromRefreshToken(refreshToken);
            String currentToken = tokenStorageManagementService
                    .get(user.getId());

            if (Strings.isNotBlank(currentToken)
                && currentToken.equals(refreshToken)) {
                final String newAccessToken = jwtTokenProvider
                        .generateAccessToken(user);
                final String newRefreshToken = jwtTokenProvider
                        .generateRefreshToken(user);

                tokenStorageManagementService
                        .save(user.getId(), newRefreshToken);
                return new JwtResponse(newAccessToken, newRefreshToken);
            }
        }
        throw new InvalidTokenException("Токен обновления не валиден.");
    }

    private User getUserFromRefreshToken(final String refreshToken) {
        Claims refreshClaims = jwtTokenProvider.getRefreshClaims(refreshToken);
        String email = refreshClaims.getSubject();
        return userManagementService.getByEmail(email);
    }
}
