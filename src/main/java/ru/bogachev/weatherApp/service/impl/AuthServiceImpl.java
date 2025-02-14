package ru.bogachev.weatherApp.service.impl;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.exception.UnauthorizedException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.security.JwtUserDetails;
import ru.bogachev.weatherApp.service.AuthService;
import ru.bogachev.weatherApp.service.TokenStorageService;
import ru.bogachev.weatherApp.service.UserService;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapper;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final TokenStorageService tokenStorageService;
    private final UserJwtEntityMapper userJwtEntityMapper;

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

            tokenStorageService.save(user.getId(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Неверный email или пароль");
        }
    }

    @Override
    public JwtResponse getAccessToken(
            @NonNull final RefreshJwtRequest request) {
        String refreshToken = request.refreshToken();

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            User user = getUserFromRefreshToken(refreshToken);
            String currentToken = tokenStorageService.get(user.getId());
            if (Strings.isNotBlank(currentToken)
                && currentToken.equals(refreshToken)) {
                final String newAccessToken = jwtTokenProvider
                        .generateAccessToken(user);
                return new JwtResponse(newAccessToken, null);
            }
        }
        throw new InvalidTokenException("Токен обновления не валиден");
    }

    @Override
    public JwtResponse refresh(@NonNull final RefreshJwtRequest request) {
        String refreshToken = request.refreshToken();

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            User user = getUserFromRefreshToken(refreshToken);
            String currentToken = tokenStorageService.get(user.getId());
            if (Strings.isNotBlank(currentToken)
                && currentToken.equals(refreshToken)) {
                final String newAccessToken = jwtTokenProvider
                        .generateAccessToken(user);
                final String newRefreshToken = jwtTokenProvider
                        .generateRefreshToken(user);

                tokenStorageService.save(user.getId(), newRefreshToken);
                return new JwtResponse(newAccessToken, newRefreshToken);
            }
        }
        throw new InvalidTokenException("Токен обновления не валиден");
    }

    private User getUserFromRefreshToken(final String refreshToken) {
        Claims refreshClaims = jwtTokenProvider.getRefreshClaims(refreshToken);
        String email = refreshClaims.getSubject();
        return userService.getByEmail(email);
    }
}
