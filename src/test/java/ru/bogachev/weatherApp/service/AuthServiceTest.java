package ru.bogachev.weatherApp.service;

import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.security.JwtUserDetails;
import ru.bogachev.weatherApp.service.impl.AuthServiceImpl;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapper;

import java.util.Set;
import java.util.stream.Collectors;

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
    private UserJwtEntityMapper jwtEntityMapper;

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
        JwtUserDetails jwtUserDetails = createJwtUserDetails(user);
        Authentication authentication = createAuthentication(jwtUserDetails);

        String expectedAccessToken = "accessToken123";
        String expectedRefreshToken = "refreshToken123";

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userService.getByEmail(request.email())).thenReturn(user);
        when(jwtEntityMapper.toDto(user)).thenReturn(jwtUserDetails);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(expectedAccessToken);
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(expectedRefreshToken);


        SignInResponse response = authService.signIn(request);

        assertNotNull(response);
        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());
    }

    @Test
    void verifySignInTest() {
        SignInRequest request = new SignInRequest("test@example.com", "password123");
        User user = createUser(request);
        JwtUserDetails jwtUserDetails = createJwtUserDetails(user);

        when(userService.getByEmail(request.email())).thenReturn(user);
        when(jwtEntityMapper.toDto(user)).thenReturn(jwtUserDetails);

        authService.signIn(request);

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtEntityMapper).toDto(user);
        verify(userService).getByEmail(request.email());
        verify(jwtTokenProvider).generateAccessToken(user);
        verify(jwtTokenProvider).generateRefreshToken(user);
    }

    @Test
    void refreshTokensSuccessTest() {
        String refreshToken = "refreshToken123";
        String email = "user@example.com";
        String newAccessToken = "newAccessToken123";
        String newRefreshToken = "newRefreshToken123";
        User mockUser = User.builder().email(email).build();
        Claims mockClaims = mock(Claims.class);

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getRefreshClaims(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.getSubject()).thenReturn(email);
        when(userService.getByEmail(email)).thenReturn(mockUser);
        when(jwtTokenProvider.generateAccessToken(mockUser)).thenReturn(newAccessToken);
        when(jwtTokenProvider.generateRefreshToken(mockUser)).thenReturn(newRefreshToken);

        SignInResponse response = authService.refreshTokens(new RefreshRequest(refreshToken));

        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(newRefreshToken, response.refreshToken());
    }

    @Test
    void verifyRefreshTokensSuccessTest() {
        String refreshToken = "refreshToken123";
        String email = "user@example.com";
        User mockUser = User.builder().email(email).build();
        Claims mockClaims = mock(Claims.class);

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getRefreshClaims(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.getSubject()).thenReturn(email);
        when(userService.getByEmail(email)).thenReturn(mockUser);

        authService.refreshTokens(new RefreshRequest(refreshToken));

        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider).getRefreshClaims(refreshToken);
        verify(mockClaims).getSubject();
        verify(userService).getByEmail(email);
        verify(jwtTokenProvider).generateAccessToken(mockUser);
    }

    @Test
    void refreshTokensFailedTest() {
        String refreshToken = "refreshToken123";

        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        InvalidTokenException exception = assertThrowsExactly(InvalidTokenException.class,
                () -> authService.refreshTokens(new RefreshRequest(refreshToken)));

        verify(jwtTokenProvider).validateRefreshToken(refreshToken);

        assertEquals("Токен обновления не валиден", exception.getMessage());
    }

    private User createUser(@NotNull SignInRequest request) {
        return User.builder()
                .id(1L)
                .email(request.email())
                .password("encodedPassword")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    @Contract("_ -> new")
    private @NotNull JwtUserDetails createJwtUserDetails(@NotNull User user) {
        return new JwtUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthority(user.getRoles())
        );
    }

    @Contract("_ -> new")
    private @NotNull Authentication createAuthentication(@NotNull JwtUserDetails jwtUserDetails) {
        return new UsernamePasswordAuthenticationToken(
                jwtUserDetails.getUsername(),
                jwtUserDetails.getPassword(),
                jwtUserDetails.getAuthorities()
        );
    }

    private Set<GrantedAuthority> mapToGrantedAuthority(@NotNull Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
