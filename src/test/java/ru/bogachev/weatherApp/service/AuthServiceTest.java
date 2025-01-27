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
import ru.bogachev.weatherApp.service.impl.AuthServiceImpl;
import ru.bogachev.weatherApp.service.impl.TokenStorageServiceImpl;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapperImpl;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

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
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenStorageServiceImpl tokenStorageService;

    @Spy
    private UserJwtEntityMapperImpl jwtEntityMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldSignUpUserSuccessfully() {
        SignUpRequest request = createSignUpRequest();

        when(passwordEncoder.encode(request.password())).thenReturn(ENCODED_PASSWORD);

        SignUpResponse response = authService.singUp(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.status());
        assertEquals("Пользователь успешно зарегистрирован", response.message());
        verify(passwordEncoder).encode(request.password());
        verify(userService).create(any(User.class));
    }

    @Test
    void shouldSignInUserSuccessfully() {
        SignInRequest request = createSignInRequest();
        User user = createUser();
        Authentication authentication = createAuthentication(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(REFRESH_TOKEN);

        JwtResponse response = authService.signIn(request);

        assertJwtResponse(response, ACCESS_TOKEN, REFRESH_TOKEN);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtEntityMapper).toEntity(any(JwtUserDetails.class));
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
        verify(tokenStorageService).save(user.getId(), REFRESH_TOKEN);
    }

    @Test
    void shouldThrowExceptionWhenSignInFailsDueToInvalidCredentials() {
        SignInRequest request = createSignInRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        UnauthorizedException exception = assertThrowsExactly(UnauthorizedException.class,
                () -> authService.signIn(request));

        assertEquals("Неверный email или пароль", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void shouldGenerateAccessTokenSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = createUser();

        setupClaims(refreshToken, user.getEmail());
        when(tokenStorageService.get(user.getId())).thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(NEW_ACCESS_TOKEN);

        JwtResponse response = authService.getAccessToken(new RefreshJwtRequest(refreshToken));

        assertJwtResponse(response, NEW_ACCESS_TOKEN, null);
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider).getRefreshClaims(refreshToken);
        verify(userService).getByEmail(user.getEmail());
        verify(tokenStorageService).get(user.getId());
        verify(jwtTokenProvider).generateAccessToken(user);
    }

    @Test
    void shouldThrowExceptionWhenGetAccessTokenIsMissing() {
        String refreshToken = "refreshToken123";
        RefreshJwtRequest request = new RefreshJwtRequest(refreshToken);

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        InvalidTokenException exception = assertThrowsExactly(InvalidTokenException.class,
                () -> authService.getAccessToken(request));

        assertEquals("Токен обновления не валиден", exception.getMessage());
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider, never()).getRefreshClaims(refreshToken);
        verify(userService, never()).getByEmail(anyString());
        verify(tokenStorageService, never()).get(anyLong());
        verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
    }


    @Test
    void shouldRefreshTokensSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = createUser();

        setupClaims(refreshToken, user.getEmail());
        when(tokenStorageService.get(user.getId())).thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(NEW_ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(NEW_REFRESH_TOKEN);

        JwtResponse response = authService.refresh(new RefreshJwtRequest(refreshToken));

        assertJwtResponse(response, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider).getRefreshClaims(refreshToken);
        verify(userService).getByEmail(DEFAULT_EMAIL);
        verify(tokenStorageService).get(user.getId());
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
        verify(tokenStorageService).save(user.getId(), NEW_REFRESH_TOKEN);
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokensIsMissing() {
        String refreshToken = REFRESH_TOKEN;
        RefreshJwtRequest request = createRefreshJwtRequest();

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        InvalidTokenException exception = assertThrowsExactly(InvalidTokenException.class,
                () -> authService.refresh(request));

        assertEquals("Токен обновления не валиден", exception.getMessage());
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider, never()).getRefreshClaims(refreshToken);
        verify(userService, never()).getByEmail(DEFAULT_EMAIL);
        verify(tokenStorageService, never()).get(anyLong());
        verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
        verify(jwtTokenProvider, never()).generateRefreshToken(any(User.class));
        verify(tokenStorageService, never()).save(anyLong(), any());
    }

    private @NotNull SignUpRequest createSignUpRequest() {
        return new SignUpRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_PASSWORD);
    }


    private @NotNull SignInRequest createSignInRequest() {
        return new SignInRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    private @NotNull RefreshJwtRequest createRefreshJwtRequest() {
        return new RefreshJwtRequest(REFRESH_TOKEN);
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .email(DEFAULT_EMAIL)
                .password(ENCODED_PASSWORD)
                .roles(Set.of(Role.ROLE_USER))
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
        when(userService.getByEmail(email)).thenReturn(createUser());
    }

    private void assertJwtResponse(JwtResponse response, String expectedAccessToken, String expectedRefreshToken) {
        assertNotNull(response);
        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());
    }
}
