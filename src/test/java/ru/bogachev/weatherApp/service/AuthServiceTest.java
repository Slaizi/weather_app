package ru.bogachev.weatherApp.service;

import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
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
    void singUpTest() {
        SignUpRequest request = createSignUpRequest();
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);

        SignUpResponse response = authService.singUp(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.status());
        assertEquals("Пользователь успешно зарегистрирован", response.message());
    }

    @Test
    void verifySignUpTest() {
        SignUpRequest request = createSignUpRequest();

        authService.singUp(request);

        verify(passwordEncoder).encode(anyString());
        verify(userService).create(any(User.class));
    }

    @Contract(" -> new")
    private @NotNull SignUpRequest createSignUpRequest() {
        return new SignUpRequest(
                "test@example.com",
                "password123",
                "password123");
    }

    @Test
    void signInTest() {
        SignInRequest request = new SignInRequest("test@example.com", "password123");
        User user = createUser(request);
        Authentication authentication = createAuthentication(user);

        String expectedAccessToken = "accessToken123";
        String expectedRefreshToken = "refreshToken123";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(expectedAccessToken);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(expectedRefreshToken);

        JwtResponse response = authService.signIn(request);

        assertNotNull(response);
        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());
    }

    @Test
    void verifySignInTest() {
        SignInRequest request = new SignInRequest("test@example.com", "password123");
        User user = createUser(request);
        Authentication authentication = createAuthentication(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        authService.signIn(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenStorageService).save(anyLong(), any());
        verify(jwtEntityMapper).toEntity(any(JwtUserDetails.class));
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
    }

    @Contract("_ -> new")
    private @NotNull Authentication createAuthentication(@NotNull User user) {
        JwtUserDetails userDetails = jwtEntityMapper.toDto(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                user.getPassword(),
                userDetails.getAuthorities()
        );
    }

    private User createUser(@NotNull SignInRequest request) {
        return User.builder()
                .id(1L)
                .email(request.email())
                .password("encodedPassword")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    @Test
    void refreshSuccessTest() {
        String refreshToken = "refreshToken123";
        String email = "user@example.com";
        String newAccessToken = "newAccessToken123";
        String newRefreshToken = "newRefreshToken123";
        User user = User.builder().id(1L).email(email).build();
        Claims mockClaims = mock(Claims.class);

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getRefreshClaims(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.getSubject()).thenReturn(email);
        when(userService.getByEmail(email)).thenReturn(user);
        when(tokenStorageService.get(user.getId())).thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(newAccessToken);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(newRefreshToken);

        JwtResponse response = authService.refresh(new RefreshJwtRequest(refreshToken));

        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(newRefreshToken, response.refreshToken());
    }

    @Test
    void verifyRefreshSuccessTest() {
        String refreshToken = "refreshToken123";
        String email = "user@example.com";
        User mockUser = User.builder().id(1L).email(email).build();
        Claims mockClaims = mock(Claims.class);

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getRefreshClaims(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.getSubject()).thenReturn(email);
        when(userService.getByEmail(email)).thenReturn(mockUser);
        when(tokenStorageService.get(anyLong())).thenReturn(refreshToken);

        authService.refresh(new RefreshJwtRequest(refreshToken));

        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider).getRefreshClaims(refreshToken);
        verify(mockClaims).getSubject();
        verify(userService).getByEmail(email);
        verify(jwtTokenProvider).generateAccessToken(any(User.class));
        verify(jwtTokenProvider).generateRefreshToken(any(User.class));
        verify(tokenStorageService).save(anyLong(), any());
    }

    @Test
    void refreshFailedTest() {
        String refreshToken = "refreshToken123";

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        InvalidTokenException exception = assertThrowsExactly(InvalidTokenException.class,
                () -> authService.refresh(new RefreshJwtRequest(refreshToken)));

        assertEquals("Токен обновления не валиден", exception.getMessage());
    }

    @Test
    void verifyRefreshFailedTest() {
        String refreshToken = "refreshToken123";

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        assertThrowsExactly(InvalidTokenException.class,
                () -> authService.refresh(new RefreshJwtRequest(refreshToken)));

        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
        verify(jwtTokenProvider, never()).generateRefreshToken(any(User.class));
        verify(tokenStorageService, never()).save(anyLong(), any());
    }
}
