package ru.bogachev.weatherApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.impl.GeolocationServiceImpl;
import ru.bogachev.weatherApp.integration.openweather.OpenWeatherApi;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.support.mapper.LocationGeoEntityMapperImpl;
import ru.bogachev.weatherApp.util.TestDataDtoFactory;
import ru.bogachev.weatherApp.util.TestDataFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GeolocationServiceTest {

    @Mock
    private OpenWeatherApi openWeatherApi;

    @Mock
    private LocationService locationService;

    @Spy
    private LocationGeoEntityMapperImpl mapper;

    @InjectMocks
    private GeolocationServiceImpl geolocationService;

    @Test
    void givenLocationExists_whenGetGeolocationByIsoCodeAndName_thenReturnsGeolocationFromDatabase() {
        String countryIsoCode = "RU";
        String nameOfLocation = "Moscow";
        Location location = TestDataFactory.createLocation();

        when(locationService.getLocation(countryIsoCode, nameOfLocation))
                .thenReturn(Optional.of(location));

        LocationGeoDto response = geolocationService
                .getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation);

        assertNotNull(response);
        assertEquals(countryIsoCode, response.getCountry());
        assertEquals(nameOfLocation, response.getName());
        assertEquals(55.7504461, response.getLatitude());
        assertEquals(37.6174943, response.getLongitude());

        verify(locationService).getLocation(countryIsoCode, nameOfLocation);
        verify(locationService, never()).create(any(Location.class));
        verifyNoInteractions(openWeatherApi);
    }

    @Test
    void givenLocationDoesNotExist_whenGetGeolocationByIsoCodeAndName_thenSavesAndReturnsGeolocation() {
        String countryIsoCode = "RU";
        String nameOfLocation = "Moscow";
        LocationGeoDto dto = TestDataDtoFactory.createLocationGeoDto();

        when(locationService.getLocation(countryIsoCode, nameOfLocation))
                .thenReturn(Optional.empty());
        when(openWeatherApi.performRequestBasedOnStrategy(
                countryIsoCode, nameOfLocation, Strategies.GET_GEOLOCATION))
                .thenReturn(dto);

        LocationGeoDto response = geolocationService
                .getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation);

        assertNotNull(response);
        assertEquals(countryIsoCode, response.getCountry());
        assertEquals(nameOfLocation, response.getName());
        assertEquals(55.7504461, response.getLatitude());
        assertEquals(37.6174943, response.getLongitude());

        verify(openWeatherApi).performRequestBasedOnStrategy(
                countryIsoCode, nameOfLocation, Strategies.GET_GEOLOCATION);
        verify(mapper).toEntity(dto);
        verify(locationService).create(any(Location.class));
    }
}
