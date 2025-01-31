package ru.bogachev.weatherApp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;
import ru.bogachev.weatherApp.exception.InvalidTokenException;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @SneakyThrows
    public void doFilter(final ServletRequest req,
                         final ServletResponse resp,
                         final FilterChain fc) {
        String token = getTokenFromRequest(req);

        try {
            if (Strings.isNotBlank(token)
                && jwtTokenProvider.validateAccessToken(token)) {
                Authentication authentication = jwtTokenProvider
                        .getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }
            fc.doFilter(req, resp);
        } catch (UsernameNotFoundException | InvalidTokenException e) {
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter()
                    .write("{\"message\": \"" + e.getMessage() + "\"}");
        }
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
