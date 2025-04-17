package ru.bogachev.weatherApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.dto.auth.AccessJwtResponse;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.RefreshJwtRequest;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.service.impl.JwtTokenServiceImpl;
import ru.bogachev.weatherApp.service.impl.TokenStorageServiceImpl;
import ru.bogachev.weatherApp.support.helper.JwtHelper;
import ru.bogachev.weatherApp.util.TestDataFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.bogachev.weatherApp.util.TestDataFactory.REFRESH_TOKEN;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    private static final String NEW_ACCESS_TOKEN = "newAccessToken123";
    private static final String NEW_REFRESH_TOKEN = "newRefreshToken123";

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenStorageServiceImpl tokenStorageService;

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private JwtTokenServiceImpl jwtTokenService;

    @Test
    void generateAccessToken_withValidToken_genSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = TestDataFactory.createUser();

        when(jwtHelper.validateAndGetUserFromToken(refreshToken))
                .thenReturn(user);
        when(jwtTokenProvider.generateAccessToken(user))
                .thenReturn(NEW_ACCESS_TOKEN);

        AccessJwtResponse response = jwtTokenService
                .generateAccessToken(new RefreshJwtRequest(refreshToken));

        assertNotNull(response);
        assertEquals(NEW_ACCESS_TOKEN, response.accessToken());

        verify(jwtHelper).validateAndGetUserFromToken(refreshToken);
        verify(jwtTokenProvider).generateAccessToken(user);
    }

    @Test
    void generateAccessAndRefreshTokens_withValidToken_genSuccessfully() {
        String refreshToken = REFRESH_TOKEN;
        User user = TestDataFactory.createUser();

        when(jwtHelper.validateAndGetUserFromToken(refreshToken))
                .thenReturn(user);
        when(jwtTokenProvider.generateAccessToken(user))
                .thenReturn(NEW_ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(user))
                .thenReturn(NEW_REFRESH_TOKEN);

        JwtResponse response = jwtTokenService
                .generateAccessAndRefreshTokens(new RefreshJwtRequest(refreshToken));

        assertNotNull(response);
        assertEquals(NEW_ACCESS_TOKEN, response.accessToken());
        assertEquals(NEW_REFRESH_TOKEN, response.refreshToken());

        verify(jwtHelper).validateAndGetUserFromToken(refreshToken);
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
        verify(tokenStorageService).save(user.getId(), NEW_REFRESH_TOKEN);
    }
}
