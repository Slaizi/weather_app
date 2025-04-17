package ru.bogachev.weatherApp.integration.openweather.request.builder;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.integration.openweather.Strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GeoByCountryAndNameBuilderTest {

    private static final String SECRET_API_KEY = "secret-key";

    @Mock
    private WeatherProperties weatherProperties;

    @InjectMocks
    private GeoByCountryAndNameBuilder geoBuilder;


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
    void whenGetStrategy_thenReturnGetGeolocationStrategy() {
        Strategies expectedStrategy = Strategies.GET_GEOLOCATION;

        Strategies result = geoBuilder.getStrategy();

        assertNotNull(result);
        assertEquals(expectedStrategy, result);
    }

    @ParameterizedTest
    @CsvSource({
            "ru, Saint-Petersburg",
            "br, Rio De Janeiro",
            "us, New York"
    })
    void givenCountryCodeAndName_whenBuildRequest_thenReturnGeolocationGetRequest(
            String countryIsoCode, String nameOfLocation
    ) {
        String url = createGeoUrlByCountryCodeNameAndSecretKey(
                countryIsoCode, nameOfLocation
        );
        HttpUrl expectedUrl = new HttpUrl.Builder()
                .parse$okhttp(null, url)
                .build();

        Request request = geoBuilder.buildRequest(countryIsoCode, nameOfLocation);

        assertNotNull(request);
        assertEquals(expectedUrl, request.url());
        assertEquals(HttpMethod.GET.name(), request.method());
    }

    @Contract(pure = true)
    private @NotNull String createGeoUrlByCountryCodeNameAndSecretKey(
            String countryIsoCode, String nameOfLocation
    ) {
        return String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s, %s&limit=1&appid=%s",
                nameOfLocation,
                countryIsoCode,
                SECRET_API_KEY
        );
    }
}
