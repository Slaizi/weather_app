package ru.bogachev.weatherApp.configuration;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.bogachev.weatherApp.security.JwtAuthenticationFilter;
import ru.bogachev.weatherApp.security.JwtTokenProvider;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(
            final @NotNull HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**",
                                "/swagger-resources/*",
                                "/v3/api-docs/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .anonymous(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setStatus(
                                            HttpStatus.UNAUTHORIZED.value());
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE);
                                    response.setCharacterEncoding(
                                            StandardCharsets.UTF_8.name());
                                    response.getWriter()
                                            .write(
                                                    "{\"message\":"
                                                    + " \"Пользователь не "
                                                    + "авторизирован.\"}"
                                            );
                                })
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setStatus(
                                            HttpStatus.FORBIDDEN.value());
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE);
                                    response.setCharacterEncoding(
                                            StandardCharsets.UTF_8.name());
                                    response.getWriter()
                                            .write(
                                                    "{\"message\": "
                                                    + "\"Доступ запрещён.\"}"
                                            );
                                })
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            final @NotNull AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

}
