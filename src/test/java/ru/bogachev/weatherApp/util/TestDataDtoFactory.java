package ru.bogachev.weatherApp.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.bogachev.weatherApp.dto.auth.SignInRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpRequest;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;

import java.util.Map;

import static ru.bogachev.weatherApp.util.TestDataFactory.DEFAULT_EMAIL;
import static ru.bogachev.weatherApp.util.TestDataFactory.DEFAULT_PASSWORD;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataDtoFactory {

    @Contract(" -> new")
    public static @NotNull SignUpRequest createSignUpRequest() {
        return new SignUpRequest(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD
        );
    }

    @Contract(" -> new")
    public static @NotNull SignInRequest createSignInRequest() {
        return new SignInRequest(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD
        );
    }

    public static @NotNull LocationGeoDto createLocationGeoDto() {
        LocationGeoDto location = new LocationGeoDto();
        location.setCountry("RU");
        location.setName("Moscow");
        location.setLatitude(55.7504461);
        location.setLongitude(37.6174943);
        location.setLocalNames(
                Map.of(
                        "en", "Moscow",
                        "ru", "Москва",
                        "nn", "Moskva"
                )
        );
        return location;
    }

}
