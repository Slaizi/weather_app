package ru.bogachev.weatherApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.exception.InvalidTokenException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JwtTokenProvider {

    private static final Long ACCESS_DURATION = 1L;
    private static final Long REFRESH_DURATION = 30L;

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;


    public JwtTokenProvider(
            @Value("${token.jwt.secret.access}")
            @NonNull final String jwtAccessSecret,
            @Value("${token.jwt.secret.refresh}")
            @NonNull final String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(
                jwtAccessSecret.getBytes());
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(
                jwtRefreshSecret.getBytes()
        );
    }

    public String generateAccessToken(@NonNull final User user) {
        Instant accessExpirationInstant = Instant.now()
                .plus(ACCESS_DURATION, ChronoUnit.HOURS)
                .atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("roles", resolveRoles(user.getRoles()))
                .claim("email", user.getEmail())
                .compact();
    }

    public String generateRefreshToken(@NonNull final User user) {
        Instant refreshExpirationInstant = Instant.now()
                .plus(REFRESH_DURATION, ChronoUnit.DAYS)
                .atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(@NonNull final String token) {
        try {
            return validateToken(token, jwtAccessSecret);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(
                    "Токен доступа недействителен или поврежден"
            );
        } catch (Exception e) {
            throw new InvalidTokenException(
                    "Произошла ошибка при проверке токена"
            );
        }
    }

    public boolean validateRefreshToken(@NonNull final String token) {
        try {
            return validateToken(token, jwtRefreshSecret);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(
                    "Токен обновления недействителен или поврежден"
            );
        } catch (Exception e) {
            throw new InvalidTokenException(
                    "Произошла ошибка при проверке токена"
            );
        }
    }

    private boolean validateToken(final String token,
                                  final SecretKey secretKey) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return !claims.getPayload()
                .getExpiration()
                .before(Date.from(Instant.now()));
    }

    public Claims getAccessClaims(@NonNull final String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull final String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(final String token,
                             final SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private List<String> resolveRoles(@Nonnull final Set<Role> roles) {
        return roles.stream().map(Enum::name).toList();
    }
}
