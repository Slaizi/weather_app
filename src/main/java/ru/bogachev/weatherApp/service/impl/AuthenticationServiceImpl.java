package ru.bogachev.weatherApp.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.SignInRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpResponse;
import ru.bogachev.weatherApp.exception.UnauthorizedException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.security.JwtUserDetails;
import ru.bogachev.weatherApp.service.AuthenticationService;
import ru.bogachev.weatherApp.service.TokenStorageService;
import ru.bogachev.weatherApp.service.UserService;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapper;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final TokenStorageService tokenStorageService;
    private final UserJwtEntityMapper userJwtEntityMapper;

    @Override
    public SignUpResponse singUp(final SignUpRequest request) {
        User user = buildUserFromRequest(request);
        return createUserAndReturnResponse(user);
    }

    private User buildUserFromRequest(final @NotNull SignUpRequest request) {
        return User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(Role.ROLE_USER))
                .registerDate(LocalDateTime.now())
                .build();
    }

    private @NotNull SignUpResponse createUserAndReturnResponse(
            final User user) {
        User dbUser = userService.create(user);
        return buildResponse(dbUser);
    }

    @Contract("_ -> new")
    private @NotNull SignUpResponse buildResponse(
            final @NotNull User dbUser) {
        return new SignUpResponse(
                dbUser.getId(),
                dbUser.getEmail(),
                dbUser.getRoles(),
                dbUser.getRegisterDate()
        );
    }

    @Override
    public JwtResponse signIn(final SignInRequest request) {
        Authentication authentication = getAuthentication(request);
        User user = getUserForAuthentication(authentication);
        return generateTokens(user);
    }

    private Authentication getAuthentication(
            final @NotNull SignInRequest request) {
        try {
            return authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()
                    )
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Неверный email или пароль.");
        }
    }

    private User getUserForAuthentication(
            @NotNull final Authentication auth) {
        JwtUserDetails userDetails =
                (JwtUserDetails) auth.getPrincipal();
        return userJwtEntityMapper.toEntity(userDetails);
    }

    @Contract("_ -> new")
    private @NotNull JwtResponse generateTokens(final User user) {
        final String accessToken = jwtTokenProvider
                .generateAccessToken(user);
        final String refreshToken = jwtTokenProvider
                .generateRefreshToken(user);

        tokenStorageService.save(user.getId(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }
}
