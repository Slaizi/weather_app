package ru.bogachev.weatherApp.integration.openweather.request.builder;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.service.GeolocationService;
import ru.bogachev.weatherApp.support.mapper.LocationGeoEntityMapperImpl;
import ru.bogachev.weatherApp.util.TestDataDtoFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherForDayBuilderTest {

    private static final String SECRET_API_KEY = "secret-key";

    @Mock
    private GeolocationService geolocationService;

    @Spy
    private LocationGeoEntityMapperImpl mapper;

    @Mock
    private WeatherProperties weatherProperties;

    @InjectMocks
    private WeatherForDayBuilder weatherBuilder;


    @BeforeEach
    void setUp() {
        WeatherProperties.Url url = new WeatherProperties.Url(
                "https://api.openweathermap.org",
                "/data/2.5/weather",
                "/geo/1.0/direct"
        );

        lenient().when(weatherProperties.getUrl()).thenReturn(url);
        lenient().when(weatherProperties.getApiKey()).thenReturn(SECRET_API_KEY);
    }

    @Test
    void whenGetStrategy_thenReturnGetWeatherForADayStrategy() {
        Strategies expectedStrategy = Strategies.GET_WEATHER_FOR_DAY;

        Strategies result = weatherBuilder.getStrategy();

        assertNotNull(result);
        assertEquals(expectedStrategy, result);
    }

    @Test
    void givenCountryAndName_whenBuildRequest_thenReturnWeatherGetRequest() {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        LocationGeoDto geolocation = TestDataDtoFactory.createLocationGeoDto();
        String url = createWeatherUrlByLocation(geolocation);
        HttpUrl expectedUrl = new HttpUrl.Builder()
                .parse$okhttp(null, url)
                .build();

        when(geolocationService.getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation))
                .thenReturn(geolocation);

        Request request = weatherBuilder.buildRequest(countryIsoCode, nameOfLocation);

        assertNotNull(request);
        assertEquals(expectedUrl, request.url());
        assertEquals(HttpMethod.GET.name(), request.method());
    }

    @Contract(pure = true)
    private @NotNull String createWeatherUrlByLocation(
            @NotNull LocationGeoDto location
    ) {
        return String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&lang=%s&units=metric&appid=%s",
                location.getLatitude(),
                location.getLongitude(),
                location.getCountry(),
                SECRET_API_KEY
        );
    }

}
