package ru.bogachev.weatherApp.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    @SneakyThrows
    public void doFilter(final ServletRequest req,
                         final ServletResponse resp,
                         final FilterChain fc) {
        String token = getTokenFromRequest(req);
        if (Strings.isNotBlank(token)
            && jwtTokenProvider.validateAccessToken(token)) {
            Claims claims = jwtTokenProvider.getAccessClaims(token);
            String username = claims.getSubject();
            try {
                authenticateUser(username);
            } catch (UsernameNotFoundException ignored) {
            }
        }
        fc.doFilter(req, resp);
    }

    @SneakyThrows
    private void authenticateUser(final String username) {
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    private String getTokenFromRequest(
            @NonNull final ServletRequest servletRequest
    ) {
        String bearer = ((HttpServletRequest) servletRequest)
                .getHeader(HEADER_NAME);

        if (StringUtils.isNotBlank(bearer)
            && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(
                    BEARER_PREFIX.length()
            );
        }
        return StringUtils.EMPTY;
    }
}
