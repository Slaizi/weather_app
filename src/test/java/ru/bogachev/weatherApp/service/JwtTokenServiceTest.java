package ru.bogachev.weatherApp.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.dto.auth.AccessJwtResponse;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.RefreshJwtRequest;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.service.impl.JwtTokenServiceImpl;
import ru.bogachev.weatherApp.service.impl.TokenStorageServiceImpl;
import ru.bogachev.weatherApp.service.impl.UserServiceImpl;
import ru.bogachev.weatherApp.util.TestDataFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.bogachev.weatherApp.util.TestDataFactory.REFRESH_TOKEN;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    private static final String NEW_ACCESS_TOKEN = "newAccessToken123";
    private static final String NEW_REFRESH_TOKEN = "newRefreshToken123";

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private TokenStorageServiceImpl tokenStorageService;
    @InjectMocks
    private JwtTokenServiceImpl jwtTokenService;

    @Test
    void getAccessToken_withValidToken_genSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = TestDataFactory.createUser();

        setupClaims(refreshToken, user.getEmail());
        when(tokenStorageService.get(user.getId()))
                .thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(user))
                .thenReturn(NEW_ACCESS_TOKEN);

        AccessJwtResponse accessToken = jwtTokenService
                .generateAccessToken(new RefreshJwtRequest(refreshToken));

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
                () -> jwtTokenService.generateAccessToken(new RefreshJwtRequest(refreshToken)));

        assertEquals("Токен обновления не валиден.", exception.getMessage());
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verifyNoInteractions(tokenStorageService);
        verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
    }

    @Test
    void getRefreshTokens_withValidToken_genSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = TestDataFactory.createUser();

        setupClaims(refreshToken, user.getEmail());
        when(tokenStorageService.get(user.getId()))
                .thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(user))
                .thenReturn(NEW_ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(user))
                .thenReturn(NEW_REFRESH_TOKEN);

        JwtResponse response = jwtTokenService
                .generateAccessAndRefreshTokens(new RefreshJwtRequest(refreshToken));

        assertNotNull(response);
        assertEquals(NEW_ACCESS_TOKEN, response.accessToken());
        assertEquals(NEW_REFRESH_TOKEN, response.refreshToken());

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
                () -> jwtTokenService
                        .generateAccessAndRefreshTokens(
                                new RefreshJwtRequest(refreshToken))
        );

        assertEquals("Токен обновления не валиден.", exception.getMessage());
        verifyNoInteractions(tokenStorageService);
        verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
        verify(jwtTokenProvider, never()).generateRefreshToken(any(User.class));
    }

    private void setupClaims(String refreshToken, String email) {
        Claims mockClaims = mock(Claims.class);
        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getRefreshClaims(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.getSubject()).thenReturn(email);
        when(userService.getByEmail(email)).thenReturn(TestDataFactory.createUser());
    }
}
