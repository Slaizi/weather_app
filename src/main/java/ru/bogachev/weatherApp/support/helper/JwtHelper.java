package ru.bogachev.weatherApp.support.helper;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtTokenProvider;
import ru.bogachev.weatherApp.service.TokenStorageService;
import ru.bogachev.weatherApp.service.UserService;

@Component
@RequiredArgsConstructor
public class JwtHelper {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TokenStorageService tokenStorageService;

    public User validateAndGetUserFromToken(final String refToken) {
        if (jwtTokenProvider.validateRefreshToken(refToken)) {
            User user = getUserFromRefToken(refToken);
            String currentRefToken = tokenStorageService
                    .get(user.getId());
            if (Strings.isNotBlank(currentRefToken)
                && currentRefToken.equals(refToken)) {
                return user;
            }
        }
        throw new InvalidTokenException("Токен обновления не валиден.");
    }

    private User getUserFromRefToken(final String refToken) {
        Claims claims = jwtTokenProvider.getRefreshClaims(refToken);
        String email = claims.getSubject();
        return userService.getByEmail(email);
    }

}
