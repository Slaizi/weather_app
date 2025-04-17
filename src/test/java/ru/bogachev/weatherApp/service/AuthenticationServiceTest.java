package ru.bogachev.weatherApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.SignInRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpResponse;
import ru.bogachev.weatherApp.exception.UnauthorizedException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.security.JwtUserDetails;
import ru.bogachev.weatherApp.service.impl.AuthenticationServiceImpl;
import ru.bogachev.weatherApp.service.impl.TokenStorageServiceImpl;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapperImpl;
import ru.bogachev.weatherApp.util.TestDataDtoFactory;
import ru.bogachev.weatherApp.util.TestDataFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.bogachev.weatherApp.util.TestDataFactory.*;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenStorageServiceImpl tokenStorageService;

    @Spy
    private UserJwtEntityMapperImpl mapper;

    @InjectMocks
    private AuthenticationServiceImpl authService;

    @Test
    void signUp_withValidUser_saveUser() {
        SignUpRequest request = TestDataDtoFactory.createSignUpRequest();
        User userInDataBase = TestDataFactory.createUser();

        when(passwordEncoder.encode(request.password()))
                .thenReturn(ENCODED_PASSWORD);
        when(userService.create(any(User.class)))
                .thenReturn(userInDataBase);

        SignUpResponse response = authService.signUp(request);

        assertNotNull(response);
        assertEquals(request.email(), response.email());
        assertEquals(Set.of(Role.ROLE_USER), response.role());
        assertEquals(userInDataBase.getId(), response.id());
        assertNotNull(response.localDateTime());

        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void signIn_withValidUser_authSuccessfully() {
        SignInRequest request = TestDataDtoFactory.createSignInRequest();

        User user = TestDataFactory.createUser();
        Authentication authentication = TestDataFactory
                .createAuthentication();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(REFRESH_TOKEN);

        JwtResponse response = authService.signIn(request);

        assertNotNull(response);
        assertEquals(ACCESS_TOKEN, response.accessToken());
        assertEquals(REFRESH_TOKEN, response.refreshToken());

        verify(mapper).toEntity(any(JwtUserDetails.class));
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
        verify(tokenStorageService).save(user.getId(), REFRESH_TOKEN);
    }

    @Test
    void signIn_withNotValidUser_throwsException() {
        SignInRequest request = TestDataDtoFactory.createSignInRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        UnauthorizedException exception = assertThrowsExactly(UnauthorizedException.class,
                () -> authService.signIn(request));
        assertEquals("Неверный email или пароль.", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider, tokenStorageService);
    }
}
