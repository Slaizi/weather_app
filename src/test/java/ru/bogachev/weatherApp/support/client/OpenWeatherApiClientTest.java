package ru.bogachev.weatherApp.support.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.support.client.strategies.ApiClientStrategy;
import ru.bogachev.weatherApp.support.client.strategies.Strategies;
import ru.bogachev.weatherApp.support.client.strategies.impl.GeolocationApiStrategy;
import ru.bogachev.weatherApp.support.client.strategies.impl.WeatherForDayApiStrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiClientTest {

    @Mock
    private GeolocationApiStrategy geolocationApiStrategy;

    @Mock
    private WeatherForDayApiStrategy weatherForDayApiStrategy;

    private OpenWeatherApiClient apiClient;


    @BeforeEach
    void setUp() {
        List<ApiClientStrategy> strategies = List.of(geolocationApiStrategy, weatherForDayApiStrategy);
        apiClient = new OpenWeatherApiClientImpl(strategies);
    }

    @Test
    void givenGeolocationStrategy_whenExecutingRequest_thenGeolocationStrategyIsUsed() {
        String countryIso = "US";
        String location = "New York";
        Strategies strategyType = Strategies.GEOLOCATION;

        when(geolocationApiStrategy.executeRequest(countryIso, location))
                .thenReturn(new LocationGeoDto());

        LocationDto result = apiClient
                .executeRequestFromStrategy(countryIso, location, strategyType);

        assertNotNull(result);
        verify(geolocationApiStrategy).executeRequest(countryIso, location);
        verifyNoInteractions(weatherForDayApiStrategy);
    }

    @Test
    void givenWeatherStrategy_whenExecutingRequest_thenWeatherStrategyIsUsed() {
        String countryIso = "US";
        String location = "New York";
        Strategies strategyType = Strategies.WEATHER_FOR_DAY;

        when(weatherForDayApiStrategy.executeRequest(countryIso, location))
                .thenReturn(new LocationWeatherDto());

        LocationDto result = apiClient
                .executeRequestFromStrategy(countryIso, location, strategyType);

        assertNotNull(result);
        verify(weatherForDayApiStrategy).executeRequest(countryIso, location);
        verifyNoInteractions(geolocationApiStrategy);
    }

}
