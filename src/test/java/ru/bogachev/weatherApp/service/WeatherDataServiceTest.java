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
import ru.bogachev.weatherApp.exception.GeoRequestException;
import ru.bogachev.weatherApp.exception.WeatherRequestException;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.impl.WeatherDataServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherDataServiceTest {

    @Mock
    private OkHttpClient client;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private WeatherProperties weatherProperties;

    @Mock
    private Call call;

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
    void getLocationGeoByName_whenRequestSuccessfully() throws IOException {
        String countyCode = "ru";
        String locationName = "Moscow";
        String jsonResponse = """
                    [
                        {
                            "name": "Moscow",
                            "local_names": {
                                "lt": "Maskva",
                                "lg": "Moosko",
                                "fa": "مسکو",
                                "fi": "Moskova",
                                "ps": "مسکو",
                                "hr": "Moskva",
                                "et": "Moskva",
                                "kk": "Мәскеу",
                                "mt": "Moska",
                                "ka": "მოსკოვი"
                            },
                            "lat": 55.7558,
                            "lon": 37.6173,
                            "country": "RU",
                            "state": "Moscow"
                        }
                    ]
                """;
        ResponseBody responseBody = ResponseBody
                .create(jsonResponse, MediaType.get("application/json"));

        Response response = new Response.Builder()
                .request(new Request.Builder()
                        .url("https://api.openweathermap.org/geo/1.0/direct")
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .code(HttpStatus.OK.value())
                .message("OK")
                .body(responseBody)
                .build();

        when(client.newCall(any(Request.class)))
                .thenReturn(call);
        when(call.execute()).thenReturn(response);

        LocationGeoDto locationGeo = weatherService
                .getLocationGeoByName(countyCode, locationName);

        assertNotNull(locationGeo);
        assertNotNull(locationGeo.getLocalNames());
        assertEquals(locationName, locationGeo.getName());
        assertEquals("RU", locationGeo.getCountry());
        assertEquals(55.7558, locationGeo.getLatitude());
        assertEquals(37.6173, locationGeo.getLongitude());
    }

    @Test
    void getLocationGeoByName_throwException() throws IOException {
        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("Network error"));

        GeoRequestException exception = assertThrows(GeoRequestException.class, () ->
                weatherService.getLocationGeoByName("ru", "Moscow")
        );

        assertEquals("Error while executing geocoding request", exception.getMessage());
    }

    @Test
    void getWeatherForLocation_whenRequestSuccessfully() throws IOException {
        Location location = new Location();
        location.setLatitude(55.7558);
        location.setLongitude(37.6173);

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
                .request(new Request.Builder()
                        .url("https://api.openweathermap.org/data/2.5/weather")
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .code(HttpStatus.OK.value())
                .message("OK")
                .body(responseBody)
                .build();

        when(call.execute()).thenReturn(response);
        when(client.newCall(any(Request.class)))
                .thenReturn(call);

        LocationWeatherDto weather = weatherService.getWeatherForLocation(location);

        assertNotNull(weather);
        assertEquals(8.09, weather.getMain().getTemperature());
        assertEquals(63, weather.getClouds().getAll());
        assertEquals("Clouds", weather.getWeathers().get(0).getCurrentState());
    }

    @Test
    void getWeatherForLocation_throwException() throws IOException {
        Location location = new Location();
        location.setLatitude(55.7558);
        location.setLongitude(37.6173);
        location.setCountry("RU");

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("Network error"));

        WeatherRequestException exception = assertThrows(WeatherRequestException.class,
                () -> weatherService.getWeatherForLocation(location));

        assertEquals("Error while executing weather request", exception.getMessage());
    }
}
