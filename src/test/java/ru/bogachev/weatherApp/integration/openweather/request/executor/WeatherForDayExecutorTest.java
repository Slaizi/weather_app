package ru.bogachev.weatherApp.integration.openweather.request.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.exception.WeatherRequestException;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.util.TestHttpObjectFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ru.bogachev.weatherApp.integration.openweather.Strategies.GET_WEATHER_FOR_DAY;
import static ru.bogachev.weatherApp.util.TestJsonResponseConstant.WEATHER_JSON;

@ExtendWith(MockitoExtension.class)
class WeatherForDayExecutorTest {

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private Call call;

    @Mock
    private OkHttpClient client;

    @InjectMocks
    private WeatherForDayExecutor weatherExecutor;

    @Test
    void whenGetStrategy_thenReturnGetWeatherForDayStrategy() {
        Strategies result = weatherExecutor.getStrategy();

        assertNotNull(result);
        assertEquals(GET_WEATHER_FOR_DAY, result);
    }

    @Test
    void whenMapResponse_thenReturnLocationWeatherDto()
            throws IOException {
        Response response = TestHttpObjectFactory
                .createResponse(WEATHER_URL, WEATHER_JSON, HttpStatus.OK, "OK");

        LocationDto weatherDto = weatherExecutor.mapResponse(response);

        assertNotNull(weatherDto);
        assertInstanceOf(LocationWeatherDto.class, weatherDto);
    }

    @Test
    void whenGetExceptionBecauseBadRequest_thenReturnWeatherRequestException() {
        Response response = TestHttpObjectFactory
                .createResponse(WEATHER_URL, "[]", HttpStatus.BAD_REQUEST, "Bad Request");

        RuntimeException exception = weatherExecutor.getExceptionBecauseBadRequest(response);

        assertNotNull(exception);
        assertInstanceOf(WeatherRequestException.class, exception);
        assertEquals("Запрос на получение погоды "
                     + "не удался. Статус: 400 (Bad Request)",
                exception.getMessage());
    }

    @Test
    void whenExecuteRequest_withBadResponse_thenThrowsWeatherRequestException()
            throws IOException {
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod(WEATHER_URL);
        Response response = TestHttpObjectFactory
                .createResponse(WEATHER_URL, "[]", HttpStatus.BAD_REQUEST, "Bad Request");

        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        assertThrows(WeatherRequestException.class,
                () -> weatherExecutor.executeRequest(request));
    }

    @Test
    void whenGetExceptionForReadingRecord_whenReturnWeatherRequestException() {
        RuntimeException exception = weatherExecutor.getExceptionForReadingRecord();

        assertNotNull(exception);
        assertInstanceOf(WeatherRequestException.class, exception);
        assertEquals("Ошибка при выполнении запроса "
                     + "на получение погоды: проблема с чтением/записью данных.",
                exception.getMessage());
    }

    @Test
    void whenExecuteRequest_withIOException_thenThrowsWeatherRequestException()
            throws IOException {
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod(WEATHER_URL);

        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenThrow(IOException.class);

        assertThrows(WeatherRequestException.class,
                () -> weatherExecutor.executeRequest(request));
    }

    @Test
    void whenExecuteRequest_thenReturnWeatherDto() throws IOException {
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod(WEATHER_URL);
        Response response = TestHttpObjectFactory
                .createResponse(WEATHER_URL, WEATHER_JSON, HttpStatus.OK, "OK");

        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        LocationDto weatherDto = weatherExecutor.executeRequest(request);

        assertNotNull(weatherDto);
        assertInstanceOf(LocationWeatherDto.class, weatherDto);
    }
}
