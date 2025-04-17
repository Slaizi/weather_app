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
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.exception.GeoNotFoundException;
import ru.bogachev.weatherApp.exception.GeoRequestException;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.util.TestHttpObjectFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ru.bogachev.weatherApp.integration.openweather.Strategies.GET_GEOLOCATION;
import static ru.bogachev.weatherApp.util.TestJsonResponseConstant.GEOLOCATION_JSON;

@ExtendWith(MockitoExtension.class)
class GeoByCountryAndNameExecutorTest {

    private static final String GEO_URL = "https://api.openweathermap.org/geo/1.0/direct";

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private Call call;

    @Mock
    private OkHttpClient client;

    @InjectMocks
    private GeoByCountryAndNameExecutor geoExecutor;


    @Test
    void whenGetStrategy_thenReturnGetGeolocationStrategy() {
        Strategies geoStrategy = geoExecutor.getStrategy();

        assertNotNull(geoStrategy);
        assertEquals(GET_GEOLOCATION, geoStrategy);
    }

    @Test
    void whenMapResponse_thenGetFirstLocationFromList()
            throws IOException {
        Response response = TestHttpObjectFactory
                .createResponse(GEO_URL, GEOLOCATION_JSON, HttpStatus.OK, "OK");

        LocationDto locationDto = geoExecutor.mapResponse(response);

        assertNotNull(locationDto);
        assertInstanceOf(LocationGeoDto.class, locationDto);
    }

    @Test
    void whenMapResponse_thenThrowGeoNotFoundException() {
        Response response = TestHttpObjectFactory
                .createResponse(GEO_URL, "[]", HttpStatus.OK, "OK");

        GeoNotFoundException exception = assertThrows(
                GeoNotFoundException.class,
                () -> geoExecutor.mapResponse(response)
        );

        assertEquals("Местоположение не было найдено, проверьте корректность параметров запроса.",
                exception.getMessage());
    }

    @Test
    void whenGetExceptionBecauseBadRequest_thenReturnGeoRequestException() {
        Response response = TestHttpObjectFactory
                .createResponse(GEO_URL, "[]", HttpStatus.BAD_REQUEST, "Bad Request");

        RuntimeException geoException = geoExecutor.getExceptionBecauseBadRequest(response);

        assertNotNull(geoException);
        assertInstanceOf(GeoRequestException.class, geoException);
        assertEquals("Запрос на получение геокодирования "
                     + "не удался. Статус: 400 (Bad Request)",
                geoException.getMessage());
    }

    @Test
    void whenExecuteRequest_withBadResponse_thenThrowsGeoRequestException()
            throws IOException {
        Request request = TestHttpObjectFactory.createRequestWithGetMethod(GEO_URL);
        Response response = TestHttpObjectFactory
                .createResponse(GEO_URL, "[]", HttpStatus.BAD_REQUEST, "Bad Request");

        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        assertThrows(GeoRequestException.class,
                () -> geoExecutor.executeRequest(request));
    }

    @Test
    void whenGetExceptionForReadingRecord_thenReturnGeoRequestException() {
        RuntimeException geoException = geoExecutor.getExceptionForReadingRecord();

        assertNotNull(geoException);
        assertInstanceOf(GeoRequestException.class, geoException);
        assertEquals("Ошибка при выполнении запроса "
                     + "геокодирования: проблема с чтением/записью данных.",
                geoException.getMessage());
    }

    @Test
    void whenExecuteRequest_withIOException_thenThrowsGeoRequestException()
            throws IOException {
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod(GEO_URL);

        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenThrow(IOException.class);

        assertThrows(GeoRequestException.class,
                () -> geoExecutor.executeRequest(request));
    }

    @Test
    void whenExecuteRequest_thenReturnGeolocationDto()
            throws IOException {
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod(GEO_URL);
        Response response = TestHttpObjectFactory
                .createResponse(GEO_URL, GEOLOCATION_JSON, HttpStatus.OK, "OK");

        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        LocationDto locationDto = geoExecutor.executeRequest(request);

        assertNotNull(locationDto);
        assertInstanceOf(LocationGeoDto.class, locationDto);
    }

}
