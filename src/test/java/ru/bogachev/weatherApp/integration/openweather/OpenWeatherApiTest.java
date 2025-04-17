package ru.bogachev.weatherApp.integration.openweather;

import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.integration.openweather.request.RequestBuilder;
import ru.bogachev.weatherApp.integration.openweather.request.RequestExecutor;
import ru.bogachev.weatherApp.integration.openweather.request.builder.GeoByCountryAndNameBuilder;
import ru.bogachev.weatherApp.integration.openweather.request.builder.WeatherForDayBuilder;
import ru.bogachev.weatherApp.integration.openweather.request.executor.GeoByCountryAndNameExecutor;
import ru.bogachev.weatherApp.integration.openweather.request.executor.WeatherForDayExecutor;
import ru.bogachev.weatherApp.util.TestDataDtoFactory;
import ru.bogachev.weatherApp.util.TestHttpObjectFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiTest {

    private static final String COUNTRY_ISO_CODE = "ru";
    private static final String NAME_OF_LOCATION = "Moscow";
    private static final String GEO_URL = "https://api.openweathermap.org/geo/1.0/direct";
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    @Mock
    private GeoByCountryAndNameBuilder geoBuilder;

    @Mock
    private WeatherForDayBuilder weatherBuilder;

    @Mock
    private GeoByCountryAndNameExecutor geoExecutor;

    @Mock
    private WeatherForDayExecutor weatherExecutor;

    private OpenWeatherApi openWeatherApi;

    @BeforeEach
    void setUp() {
        when(geoBuilder.getStrategy())
                .thenReturn(Strategies.GET_GEOLOCATION);
        when(geoExecutor.getStrategy())
                .thenReturn(Strategies.GET_GEOLOCATION);

        when(weatherBuilder.getStrategy())
                .thenReturn(Strategies.GET_WEATHER_FOR_DAY);
        when(weatherExecutor.getStrategy())
                .thenReturn(Strategies.GET_WEATHER_FOR_DAY);

        List<RequestBuilder> builders = List.of(geoBuilder, weatherBuilder);
        List<RequestExecutor> executors = List.of(geoExecutor, weatherExecutor);
        this.openWeatherApi = new OpenWeatherApiImpl(builders, executors);
    }

    @Test
    void whenRequestingGeolocationWithCountryAndName_thenGeolocationStrategyIsApplied() {
        String countryIsoCode = COUNTRY_ISO_CODE;
        String nameOfLocation = NAME_OF_LOCATION;
        Strategies strategy = Strategies.GET_GEOLOCATION;
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod(GEO_URL);
        LocationGeoDto geolocation = TestDataDtoFactory.createLocationGeoDto();

        when(geoBuilder.buildRequest(countryIsoCode, nameOfLocation))
                .thenReturn(request);
        when(geoExecutor.executeRequest(request))
                .thenReturn(geolocation);

        LocationDto response = openWeatherApi.performRequestBasedOnStrategy(
                countryIsoCode, nameOfLocation, strategy
        );

        assertNotNull(response);
        assertInstanceOf(LocationGeoDto.class, response);

        verify(geoBuilder).buildRequest(countryIsoCode, nameOfLocation);
        verify(geoExecutor).executeRequest(request);
        verify(weatherBuilder, never()).buildRequest(countryIsoCode, nameOfLocation);
        verify(weatherExecutor, never()).executeRequest(any(Request.class));
    }

    @Test
    void whenRequestingWeatherWithCountryAndName_thenWeatherStrategyIsApplied() {
        String countryIsoCode = COUNTRY_ISO_CODE;
        String nameOfLocation = NAME_OF_LOCATION;
        Strategies strategy = Strategies.GET_WEATHER_FOR_DAY;
        Request request = TestHttpObjectFactory
                .createRequestWithGetMethod(WEATHER_URL);
        LocationWeatherDto weather = TestDataDtoFactory.createLocationWeatherDto();

        when(weatherBuilder.buildRequest(countryIsoCode, nameOfLocation))
                .thenReturn(request);
        when(weatherExecutor.executeRequest(request))
                .thenReturn(weather);

        LocationDto response = openWeatherApi.performRequestBasedOnStrategy(
                countryIsoCode, nameOfLocation, strategy
        );

        assertNotNull(response);
        assertInstanceOf(LocationWeatherDto.class, response);

        verify(weatherBuilder).buildRequest(countryIsoCode, nameOfLocation);
        verify(weatherExecutor).executeRequest(request);
        verify(geoBuilder, never()).buildRequest(countryIsoCode, nameOfLocation);
        verify(geoExecutor, never()).executeRequest(any(Request.class));
    }

    @Test
    void whenRequestingGeolocationWithIncompleteBuilders_thenThrowsIllegalArgumentException() {
        List<RequestBuilder> builders = List.of(weatherBuilder);
        List<RequestExecutor> executors = List.of(geoExecutor, weatherExecutor);
        openWeatherApi = new OpenWeatherApiImpl(builders, executors);

        assertThrows(IllegalArgumentException.class, () ->
                openWeatherApi.performRequestBasedOnStrategy(
                        COUNTRY_ISO_CODE, NAME_OF_LOCATION, Strategies.GET_GEOLOCATION
                ));
    }

    @Test
    void whenRequestingWeatherWithIncompleteExecutors_thenThrowsIllegalArgumentException() {
        List<RequestBuilder> builders = List.of(geoBuilder, weatherBuilder);
        List<RequestExecutor> executors = List.of(geoExecutor);
        openWeatherApi = new OpenWeatherApiImpl(builders, executors);

        assertThrows(IllegalArgumentException.class, () ->
                openWeatherApi.performRequestBasedOnStrategy(
                        COUNTRY_ISO_CODE, NAME_OF_LOCATION, Strategies.GET_WEATHER_FOR_DAY
                ));
    }
}
