package ru.bogachev.weatherApp.service;

import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
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
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.exception.UnauthorizedException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.security.JwtUserDetails;
import ru.bogachev.weatherApp.service.impl.AuthenticationServiceImpl;
import ru.bogachev.weatherApp.service.impl.TokenStorageManagementServiceImpl;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapperImpl;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String DEFAULT_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String ACCESS_TOKEN = "accessToken123";
    private static final String REFRESH_TOKEN = "refreshToken123";
    private static final String NEW_ACCESS_TOKEN = "newAccessToken123";
    private static final String NEW_REFRESH_TOKEN = "newRefreshToken123";

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenStorageManagementServiceImpl tokenStorageService;

    @Spy
    private UserJwtEntityMapperImpl jwtEntityMapper;

    @InjectMocks
    private AuthenticationServiceImpl authService;

    @Test
    void signUp_withValidUser_saveUser() {
        SignUpRequest request =
                new SignUpRequest(
                        DEFAULT_EMAIL,
                        DEFAULT_PASSWORD,
                        DEFAULT_PASSWORD
                );
        User userInDataBase = createUser();

        when(passwordEncoder.encode(request.password())).thenReturn(ENCODED_PASSWORD);
        when(userManagementService.create(any(User.class))).thenReturn(userInDataBase);

        SignUpResponse response = authService.singUp(request);

        assertNotNull(response);
        assertEquals(request.email(), response.email());
        assertEquals(Set.of(Role.ROLE_USER), response.role());
        assertEquals(userInDataBase.getId(), response.id());
        assertNotNull(response.localDateTime());

        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void signIn_withValidUser_authSuccessfully() {
        SignInRequest request = createSignInRequest();
        User user = createUser();
        Authentication authentication = createAuthentication(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(REFRESH_TOKEN);

        JwtResponse response = authService.signIn(request);

        assertJwtResponse(response, ACCESS_TOKEN, REFRESH_TOKEN);
        verify(jwtEntityMapper).toEntity(any(JwtUserDetails.class));
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
        verify(tokenStorageService).save(user.getId(), REFRESH_TOKEN);
    }

    @Test
    void signIn_withNotValidUser_throwsException() {
        SignInRequest request = createSignInRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        UnauthorizedException exception = assertThrowsExactly(UnauthorizedException.class,
                () -> authService.signIn(request));
        assertEquals("Неверный email или пароль.", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider, tokenStorageService);
    }

    @Test
    void getAccessToken_withValidToken_genSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = createUser();

        setupClaims(refreshToken, user.getEmail());
        when(tokenStorageService.get(user.getId()))
                .thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(user))
                .thenReturn(NEW_ACCESS_TOKEN);

        AccessJwtResponse accessToken = authService
                .getAccessToken(new RefreshJwtRequest(refreshToken));

        assertEquals(NEW_ACCESS_TOKEN, accessToken.accessToken());
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(tokenStorageService).get(user.getId());
        verify(jwtTokenProvider).generateAccessToken(user);
    }

    @Test
    void getAccessToken_withNoValidToken_throwException() {
        String refreshToken = REFRESH_TOKEN;

        when(jwtTokenProvider.validateRefreshToken(refreshToken))
                .thenReturn(false);

        InvalidTokenException exception = assertThrowsExactly(InvalidTokenException.class,
                () -> authService.getAccessToken(new RefreshJwtRequest(refreshToken)));

        assertEquals("Токен обновления не валиден.", exception.getMessage());
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verifyNoInteractions(tokenStorageService);
        verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
    }

    @Test
    void getRefreshTokens_withValidToken_genSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = createUser();

        setupClaims(refreshToken, user.getEmail());
        when(tokenStorageService.get(user.getId()))
                .thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(user))
                .thenReturn(NEW_ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(user))
                .thenReturn(NEW_REFRESH_TOKEN);

        JwtResponse response = authService
                .getRefreshTokens(new RefreshJwtRequest(refreshToken));

        assertJwtResponse(response, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
        verify(tokenStorageService).save(user.getId(), NEW_REFRESH_TOKEN);
    }

    @Test
    void getRefreshTokens_withInvalidToken_throwException() {
        String refreshToken = REFRESH_TOKEN;

        when(jwtTokenProvider.validateRefreshToken(refreshToken))
                .thenReturn(false);

        InvalidTokenException exception = assertThrowsExactly(InvalidTokenException.class,
                () -> authService.getRefreshTokens(new RefreshJwtRequest(refreshToken)));

        assertEquals("Токен обновления не валиден.", exception.getMessage());
        verifyNoInteractions(tokenStorageService);
        verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
        verify(jwtTokenProvider, never()).generateRefreshToken(any(User.class));
    }


    private @NotNull SignInRequest createSignInRequest() {
        return new SignInRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .email(DEFAULT_EMAIL)
                .password(ENCODED_PASSWORD)
                .roles(Set.of(Role.ROLE_USER))
                .registerDate(LocalDateTime.now())
                .build();
    }

    private @NotNull Authentication createAuthentication(User user) {
        JwtUserDetails userDetails = jwtEntityMapper.toDto(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                user.getPassword(),
                userDetails.getAuthorities()
        );
    }

    private void setupClaims(String refreshToken, String email) {
        Claims mockClaims = mock(Claims.class);
        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getRefreshClaims(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.getSubject()).thenReturn(email);
        when(userManagementService.getByEmail(email)).thenReturn(createUser());
    }

    private void assertJwtResponse(JwtResponse response, String expectedAccessToken, String expectedRefreshToken) {
        assertNotNull(response);
        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());
    }
}
