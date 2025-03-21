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
import ru.bogachev.weatherApp.exception.GeoNotFoundException;
import ru.bogachev.weatherApp.exception.GeoRequestException;
import ru.bogachev.weatherApp.support.builder.HttpRequestBuilder;
import ru.bogachev.weatherApp.support.client.strategies.impl.GeolocationApiStrategy;
import ru.bogachev.weatherApp.util.TestHttpObjectFactory;
import ru.bogachev.weatherApp.util.TestJsonResponseConstant;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeolocationApiStrategyTest {

    @Mock
    private OkHttpClient client;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private HttpRequestBuilder requestBuilder;

    @Mock
    private Call call;

    @InjectMocks
    private GeolocationApiStrategy geoStrategy;

    @Test
    void givenValidCountryAndLocation_whenExecutingGeolocationRequest_thenReturnsLocationGeoDto()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/geo/1.0/direct");
        ResponseBody responseBody = TestHttpObjectFactory
                .createResponseBody(TestJsonResponseConstant.GEOLOCATION_JSON);
        Response response = TestHttpObjectFactory
                .createResponse(request, HttpStatus.OK, "OK", responseBody);

        when(requestBuilder.buildGeocodingRequest(countryIsoCode, nameOfLocation))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        LocationDto dto = geoStrategy
                .executeRequest(countryIsoCode, nameOfLocation);

        assertNotNull(dto);
        assertInstanceOf(LocationGeoDto.class, dto);

        verify(requestBuilder).buildGeocodingRequest(countryIsoCode, nameOfLocation);
        verify(client).newCall(request);
    }

    @Test
    void givenEmptyResponse_whenExecutingGeolocationRequest_thenThrowsGeoNotFoundException()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/geo/1.0/direct");
        ResponseBody responseBody = TestHttpObjectFactory
                .createResponseBody("[]");
        Response response = TestHttpObjectFactory
                .createResponse(request, HttpStatus.OK, "OK", responseBody);

        when(requestBuilder.buildGeocodingRequest(countryIsoCode, nameOfLocation))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        GeoNotFoundException exception = assertThrowsExactly(GeoNotFoundException.class, () ->
                geoStrategy.executeRequest(countryIsoCode, nameOfLocation));
        assertEquals("Местоположение не было найдено, "
                     + "проверьте корректность параметров запроса.", exception.getMessage());
        verify(requestBuilder).buildGeocodingRequest(countryIsoCode, nameOfLocation);
        verify(client).newCall(request);
    }

    @Test
    void givenRequestExecutionError_whenExecutingGeolocationRequest_thenThrowsGeoRequestException()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/geo/1.0/direct");

        when(requestBuilder.buildGeocodingRequest(countryIsoCode, nameOfLocation))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenThrow(new IOException("Ошибка сети"));

        GeoRequestException exception = assertThrowsExactly(GeoRequestException.class, () ->
                geoStrategy.executeRequest(countryIsoCode, nameOfLocation)
        );
        assertEquals("Ошибка при выполнении запроса геокодирования: проблема с чтением/записью данных.",
                exception.getMessage());
        verify(requestBuilder).buildGeocodingRequest(countryIsoCode, nameOfLocation);
        verify(client).newCall(request);
    }

    @Test
    void givenNotFoundResponse_whenExecutingGeolocationRequest_thenThrowsGeoRequestException()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "UnknownCity";
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/geo/1.0/direct");
        Response response = TestHttpObjectFactory
                .createResponse(request, HttpStatus.NOT_FOUND, "Not Found", null);

        when(requestBuilder.buildGeocodingRequest(countryIsoCode, nameOfLocation))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);


        GeoRequestException exception = assertThrowsExactly(GeoRequestException.class, () ->
                geoStrategy.executeRequest(countryIsoCode, nameOfLocation));
        assertEquals("Запрос на получение геокодирования не удался. Статус: 404 (Not Found)",
                exception.getMessage());
        verify(requestBuilder).buildGeocodingRequest(countryIsoCode, nameOfLocation);
        verify(client).newCall(request);
    }

    @Test
    void givenServerErrorResponse_whenExecutingGeolocationRequest_thenThrowsGeoRequestException()
            throws IOException {
        String countryIsoCode = "ru";
        String nameOfLocation = "Moscow";
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod("https://api.openweathermap.org/geo/1.0/direct");
        Response response = TestHttpObjectFactory
                .createResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null);

        when(requestBuilder.buildGeocodingRequest(countryIsoCode, nameOfLocation))
                .thenReturn(request);
        when(client.newCall(request))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(response);

        GeoRequestException exception = assertThrowsExactly(GeoRequestException.class, () ->
                geoStrategy.executeRequest(countryIsoCode, nameOfLocation));
        assertEquals("Запрос на получение геокодирования не удался. Статус: 500 (Server Error)",
                exception.getMessage());
        verify(requestBuilder).buildGeocodingRequest(countryIsoCode, nameOfLocation);
        verify(client).newCall(request);
    }

}
