package ru.bogachev.weatherApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.repository.LocationRepository;
import ru.bogachev.weatherApp.service.impl.LocationServiceImpl;
import ru.bogachev.weatherApp.util.TestDataFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void givenExistingLocation_whenCreate_thenSaveFails() {
        Location location = TestDataFactory.createLocation();

        when(locationRepository
                .existsByCountryAndCity(location.getCountry(), location.getCityName()))
                .thenReturn(true);

        Location response = locationService
                .create(location);

        assertNotNull(response);
        assertEquals(location, response);

        verify(locationRepository, never()).save(location);
    }

    @Test
    void givenAbsentLocation_whenCreate_thenSaveSucceeds() {
        Location location = TestDataFactory.createLocation();

        when(locationRepository
                .existsByCountryAndCity(location.getCountry(), location.getCityName()))
                .thenReturn(false);
        when(locationRepository.save(location))
                .thenReturn(location);

        Location response = locationService
                .create(location);

        assertNotNull(response);
        assertEquals(location, response);

        verify(locationRepository).save(location);
    }

    @Test
    void givenExistingLocation_whenGetLocation_thenReturnsLocation() {
        String countryIsoCode = "RU";
        String nameOfLocation = "mOscOW";
        String correctedNameLocation = "Moscow";
        Location location = TestDataFactory.createLocation();

        when(locationRepository.findLocationByJsonbValue(countryIsoCode, correctedNameLocation))
                .thenReturn(Optional.of(location));

        Optional<Location> result = locationService.getLocation(countryIsoCode, nameOfLocation);

        assertTrue(result.isPresent());
        assertEquals(location, result.get());
        verify(locationRepository).findLocationByJsonbValue(countryIsoCode, correctedNameLocation);
    }

    @Test
    void givenNonExistingLocation_whenGetLocation_thenReturnsEmptyOptional() {
        String countryIsoCode = "RU";
        String nameOfLocation = "unkNoWn city";
        String correctedNameLocation = "Unknown City";

        when(locationRepository.findLocationByJsonbValue(countryIsoCode, correctedNameLocation))
                .thenReturn(Optional.empty());

        Optional<Location> result = locationService.getLocation(countryIsoCode, nameOfLocation);

        assertTrue(result.isEmpty());
        verify(locationRepository).findLocationByJsonbValue(countryIsoCode, correctedNameLocation);
    }
}
