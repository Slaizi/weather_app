package ru.bogachev.weatherApp.support.builder;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.util.TestDataFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpRequestBuilderTest {

    @Mock
    private WeatherProperties weatherProperties;

    @InjectMocks
    private HttpRequestBuilder builder;

    @BeforeEach
    void setUp() {
        WeatherProperties.Url url = new WeatherProperties.Url(
                "https://api.openweathermap.org",
                "/data/2.5/weather",
                "/geo/1.0/direct"
        );
        when(weatherProperties.getUrl()).thenReturn(url);
        when(weatherProperties.getApiKey()).thenReturn("test-api-key");
    }

    @Test
    void givenCountryAndCity_whenBuildGeocodingRequest_thenReturnsValidRequest() {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        String url = "https://api.openweathermap.org/geo/1.0/direct?q=" + nameOfLocation
                     + ", " + countryIsoCode + "&limit=1&appid=test-api-key";

        Request request = builder
                .buildGeocodingRequest(countryIsoCode, nameOfLocation);

        assertionsValidGetRequest(url, request);
    }

    @Test
    void givenLocation_whenBuildWeatherCityRequest_thenReturnsValidRequest() {
        Location loc = TestDataFactory.createLocation();
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="
                     + loc.getLatitude() + "&lon=" + loc.getLongitude()
                     + "&lang=" + loc.getCountry() + "&units=metric"
                     + "&appid=test-api-key";


        Request request = builder
                .buildWeatherCityRequest(loc);

        assertionsValidGetRequest(url, request);
    }

    private void assertionsValidGetRequest(String url, Request request) {
        HttpUrl expectedUrl = new HttpUrl.Builder()
                .parse$okhttp(null, url)
                .build();

        assertNotNull(request);
        assertEquals("GET", request.method());
        assertEquals(expectedUrl, request.url());
    }
}
