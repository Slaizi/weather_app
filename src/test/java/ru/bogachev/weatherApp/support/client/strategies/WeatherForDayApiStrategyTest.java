package ru.bogachev.weatherApp.support.client.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.exception.WeatherRequestException;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.GeolocationService;
import ru.bogachev.weatherApp.support.builder.HttpRequestBuilder;
import ru.bogachev.weatherApp.support.client.strategies.impl.WeatherForDayApiStrategy;
import ru.bogachev.weatherApp.support.mapper.LocationGeoEntityMapperImpl;
import ru.bogachev.weatherApp.util.TestDataDtoFactory;
import ru.bogachev.weatherApp.util.TestHttpObjectFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.bogachev.weatherApp.util.TestJsonResponseConstant.WEATHER_JSON;

@ExtendWith(MockitoExtension.class)
class WeatherForDayApiStrategyTest {

    @Mock
    private OkHttpClient client;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private Call call;

    @Mock
    private HttpRequestBuilder requestBuilder;

    @Mock
    private GeolocationService geolocationService;

    @Spy
    private LocationGeoEntityMapperImpl mapper;

    @InjectMocks
    private WeatherForDayApiStrategy weatherStrategy;

    @Test
    void givenCountryAndLocation_whenExecutingWeatherRequest_thenReturnWeatherDto()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        LocationGeoDto dto = TestDataDtoFactory.createLocationGeoDto();
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/data/2.5/weather");
        ResponseBody responseBody = TestHttpObjectFactory
                .createResponseBody(WEATHER_JSON);
        Response response = TestHttpObjectFactory
                .createResponse(request, HttpStatus.OK, "OK", responseBody);

        when(geolocationService.getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation))
                .thenReturn(dto);
        when(requestBuilder.buildWeatherCityRequest(any(Location.class)))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        LocationDto locationDto = weatherStrategy
                .executeRequest(countryIsoCode, nameOfLocation);

        assertNotNull(locationDto);
        assertInstanceOf(LocationWeatherDto.class, locationDto);

        verify(geolocationService).getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation);
        verify(requestBuilder).buildWeatherCityRequest(any(Location.class));
        verify(client).newCall(request);
    }

    @Test
    void givenRequestExecutionError_whenExecutingWeatherRequest_thenThrowsWeatherRequestException()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        LocationGeoDto dto = TestDataDtoFactory.createLocationGeoDto();
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/data/2.5/weather");

        when(geolocationService.getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation))
                .thenReturn(dto);
        when(requestBuilder.buildWeatherCityRequest(any(Location.class)))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenThrow(new IOException("Ошибка сети"));

        WeatherRequestException exception = assertThrowsExactly(WeatherRequestException.class, () ->
                weatherStrategy.executeRequest(countryIsoCode, nameOfLocation)
        );
        assertEquals("Ошибка при выполнении запроса на получение погоды: проблема с чтением/записью данных.",
                exception.getMessage());

        verify(requestBuilder).buildWeatherCityRequest(any(Location.class));
        verify(client).newCall(request);
    }

    @Test
    void givenNotFoundResponse_whenExecutingWeatherRequest_thenThrowsWeatherRequestException()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        LocationGeoDto dto = TestDataDtoFactory.createLocationGeoDto();
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/data/2.5/weather");
        Response response = TestHttpObjectFactory
                .createResponse(request, HttpStatus.NOT_FOUND, "Not Found", null);

        when(geolocationService.getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation))
                .thenReturn(dto);
        when(requestBuilder.buildWeatherCityRequest(any(Location.class)))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        WeatherRequestException exception = assertThrowsExactly(WeatherRequestException.class, () ->
                weatherStrategy.executeRequest(countryIsoCode, nameOfLocation));
        assertEquals("Запрос на получение погоды не удался. Статус: 404 (Not Found)",
                exception.getMessage());

        verify(requestBuilder).buildWeatherCityRequest(any(Location.class));
        verify(client).newCall(request);
    }

    @Test
    void givenServerErrorResponse_whenExecutingWeatherRequest_thenThrowsWeatherRequestException()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        LocationGeoDto dto = TestDataDtoFactory.createLocationGeoDto();
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/data/2.5/weather");
        Response response = TestHttpObjectFactory
                .createResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null);

        when(geolocationService.getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation))
                .thenReturn(dto);
        when(requestBuilder.buildWeatherCityRequest(any(Location.class)))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        WeatherRequestException exception = assertThrowsExactly(WeatherRequestException.class, () ->
                weatherStrategy.executeRequest(countryIsoCode, nameOfLocation));
        assertEquals("Запрос на получение погоды не удался. Статус: 500 (Server Error)",
                exception.getMessage());

        verify(requestBuilder).buildWeatherCityRequest(any(Location.class));
        verify(client).newCall(request);
    }
}
