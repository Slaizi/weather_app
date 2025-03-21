package ru.bogachev.weatherApp.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.model.location.LocationNames;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtUserDetails;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataFactory {

    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final String ACCESS_TOKEN = "accessToken123";
    public static final String REFRESH_TOKEN = "refreshToken123";

    public static User createUser() {
        return User.builder()
                .id(1L)
                .email(DEFAULT_EMAIL)
                .password(ENCODED_PASSWORD)
                .roles(Set.of(Role.ROLE_USER))
                .registerDate(LocalDateTime.now())
                .build();
    }

    public static @NotNull Location createLocation() {
        Location location = new Location();
        location.setCountry("RU");
        location.setCityName("Moscow");
        location.setLatitude(55.7504461);
        location.setLongitude(37.6174943);
        location.setLocalNames(
                new LocationNames(
                        Map.of(
                                "en", "Moscow",
                                "ru", "Москва",
                                "nn", "Moskva"
                        )
                )
        );
        return location;
    }

    public static @NotNull Authentication createAuthentication() {
        User user = createUser();
        JwtUserDetails userDetails = createUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

    @Contract("_ -> new")
    private static @NotNull JwtUserDetails createUserDetails(@NotNull User user) {
        return new JwtUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                mapUserRoles(user.getRoles())
        );
    }

    private static Set<SimpleGrantedAuthority> mapUserRoles(@NotNull Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
