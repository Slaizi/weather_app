package ru.bogachev.weatherApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.impl.WeatherDataServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherDataServiceTest {

    @Mock
    private OkHttpClient client;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private WeatherProperties weatherProperties;

    @InjectMocks
    private WeatherDataServiceImpl weatherService;

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
    void getLocationGeoByNameTest() throws IOException {
        String locationName = "Moscow";
        String jsonResponse = """
                    [
                        {
                            "name": "Moscow",
                            "lat": 55.7558,
                            "lon": 37.6173
                        }
                    ]
                """;

        ResponseBody responseBody = ResponseBody
                .create(jsonResponse, MediaType.get("application/json"));

        Response response = new Response.Builder()
                .request(new Request.Builder().url("https://mock-url.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(HttpStatus.OK.value())
                .message("OK")
                .body(responseBody)
                .build();

        Call mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(response);
        when(client.newCall(any(Request.class))).thenReturn(mockCall);

        LocationGeoDto locationGeo = weatherService.getLocationGeoByName(locationName);

        assertNotNull(locationGeo);
        assertEquals("Moscow", locationGeo.getName());
        assertEquals(55.7558, locationGeo.getLatitude());
        assertEquals(37.6173, locationGeo.getLongitude());
    }

    @Test
    void getWeatherForLocationTest() throws IOException {
        Double latitude = 55.7558;
        Double longitude = 37.6173;
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);


        String jsonResponse = """
                {
                    "coord": {
                        "lon": 37.6234,
                        "lat": 55.7621
                    },
                    "weather": [
                        {
                            "id": 803,
                            "main": "Clouds",
                            "description": "облачно с прояснениями",
                            "icon": "04d"
                        }
                    ],
                    "base": "stations",
                    "main": {
                        "temp": 8.09,
                        "feels_like": 5.62,
                        "temp_min": 7.18,
                        "temp_max": 8.48,
                        "pressure": 1021,
                        "humidity": 63,
                        "sea_level": 1021,
                        "grnd_level": 1001
                    },
                    "visibility": 10000,
                    "wind": {
                        "speed": 4.08,
                        "deg": 236,
                        "gust": 9.07
                    },
                    "clouds": {
                        "all": 63
                    },
                    "dt": 1738233424,
                    "sys": {
                        "type": 2,
                        "id": 2095214,
                        "country": "RU",
                        "sunrise": 1738214829,
                        "sunset": 1738245496
                    },
                    "timezone": 10800,
                    "id": 524901,
                    "name": "Москва",
                    "cod": 200
                }
                """;
        ResponseBody responseBody = ResponseBody
                .create(jsonResponse, MediaType.get("application/json"));

        Response response = new Response.Builder()
                .request(new Request.Builder().url("https://mock-url.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(HttpStatus.OK.value())
                .message("OK")
                .body(responseBody)
                .build();

        Call mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(response);
        when(client.newCall(any(Request.class))).thenReturn(mockCall);

        LocationWeatherDto weather = weatherService.getWeatherForLocation(location);
        assertNotNull(weather);
        assertEquals(8.09, weather.getMain().getTemperature());
        assertEquals(7.18, weather.getMain().getTemperatureMin());
        assertEquals(8.48, weather.getMain().getTemperatureMax());
        assertEquals(63, weather.getClouds().getAll());
        assertEquals("Clouds", weather.getWeathers().get(0).getCurrentState());
    }
}
