package ru.bogachev.weatherApp.support.helper;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.service.impl.TokenStorageServiceImpl;
import ru.bogachev.weatherApp.service.impl.UserServiceImpl;
import ru.bogachev.weatherApp.util.TestDataFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static ru.bogachev.weatherApp.util.TestDataFactory.DEFAULT_EMAIL;
import static ru.bogachev.weatherApp.util.TestDataFactory.REFRESH_TOKEN;

@ExtendWith(MockitoExtension.class)
class JwtHelperTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private TokenStorageServiceImpl tokenStorageService;
    @InjectMocks
    private JwtHelper jwtHelper;

    @Test
    void validateRefreshTokenAndGetUser_validToken_successfully() {
        String refToken = REFRESH_TOKEN;
        User user = TestDataFactory.createUser();

        setupClaims(refToken);
        when(jwtTokenProvider.validateRefreshToken(refToken))
                .thenReturn(true);
        when(tokenStorageService.get(user.getId()))
                .thenReturn(refToken);

        User response = jwtHelper.validateAndGetUserFromToken(refToken);
        assertNotNull(response);
        assertEquals(user, response);

        verify(jwtTokenProvider).validateRefreshToken(refToken);
        verify(tokenStorageService).get(user.getId());
    }

    @Test
    void validateRefreshTokenAndGetUser_invalidToken_throwException() {
        String refToken = REFRESH_TOKEN;

        when(jwtTokenProvider.validateRefreshToken(refToken))
                .thenReturn(false);

        InvalidTokenException exception = assertThrowsExactly(InvalidTokenException.class,
                () -> jwtHelper.validateAndGetUserFromToken(refToken));

        assertEquals("Токен обновления не валиден.", exception.getMessage());

        verify(jwtTokenProvider, never()).getRefreshClaims(refToken);
        verify(userService, never()).getByEmail(any(String.class));
        verifyNoInteractions(tokenStorageService);
    }

    private void setupClaims(String refToken) {
        Claims claims = mock(Claims.class);
        when(jwtTokenProvider.getRefreshClaims(refToken))
                .thenReturn(claims);
        when(claims.getSubject())
                .thenReturn(DEFAULT_EMAIL);
        when(userService.getByEmail(DEFAULT_EMAIL))
                .thenReturn(TestDataFactory.createUser());
    }
}
