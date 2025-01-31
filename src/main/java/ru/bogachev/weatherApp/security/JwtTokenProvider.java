package ru.bogachev.weatherApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    private final Long accessDuration;
    private final Long refreshDuration;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("${token.jwt.secret.access.value}")
            @NonNull final String jwtAccessSecret,
            @Value("${token.jwt.secret.refresh.value}")
            @NonNull final String jwtRefreshSecret,
            @Value("${token.jwt.secret.access.duration}")
            @NonNull final Long accessDuration,
            @Value("${token.jwt.secret.refresh.duration}")
            @NonNull final Long refreshDuration,
            final UserDetailsService userDetailsService
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(
                jwtAccessSecret.getBytes());
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(
                jwtRefreshSecret.getBytes()
        );
        this.accessDuration = accessDuration;
        this.refreshDuration = refreshDuration;
        this.userDetailsService = userDetailsService;
    }

    public String generateAccessToken(@NonNull final User user) {
        Instant accessExpirationInstant = Instant.now()
                .plus(accessDuration, ChronoUnit.HOURS)
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
                .plus(refreshDuration, ChronoUnit.DAYS)
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

    public Authentication getAuthentication(
            @NonNull final String token) {
        Claims claims = getAccessClaims(token);
        String email = claims.getSubject();
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    private List<String> resolveRoles(@Nonnull final Set<Role> roles) {
        return roles.stream().map(Enum::name).toList();
    }
}
