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
import ru.bogachev.weatherApp.service.impl.DataWeatherServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataWeatherServiceTest {

    @Mock
    private OkHttpClient client;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private WeatherProperties weatherProperties;

    @InjectMocks
    private DataWeatherServiceImpl weatherService;

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
}
