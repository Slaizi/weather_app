package ru.bogachev.weatherApp.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.bogachev.weatherApp.dto.auth.SignInRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpRequest;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.dto.location.node.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
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

    public static @NotNull LocationWeatherDto createLocationWeatherDto() {
        LocationWeatherDto weatherDto = new LocationWeatherDto();
        weatherDto.setWeathers(List.of(new Weather(803, "Clouds", "облачно с прояснениями")));
        weatherDto.setMain(new Main(8.09, 5.62, 7.18, 8.48, 1021, 63));
        weatherDto.setWind(new Wind(4.08, 236, 9.07));
        weatherDto.setClouds(new Clouds(63));

        weatherDto.setDateTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(1738233424), ZoneId.systemDefault()));

        weatherDto.setSys(new Sys(
                LocalDateTime.ofInstant(Instant.ofEpochSecond(1738214829), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.ofEpochSecond(1738245496), ZoneId.systemDefault())
        ));

        return weatherDto;
    }

}
