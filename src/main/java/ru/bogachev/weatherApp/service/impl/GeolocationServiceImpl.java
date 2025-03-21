package ru.bogachev.weatherApp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.GeolocationService;
import ru.bogachev.weatherApp.service.LocationService;
import ru.bogachev.weatherApp.support.client.OpenWeatherApiClient;
import ru.bogachev.weatherApp.support.client.strategies.Strategies;
import ru.bogachev.weatherApp.support.mapper.LocationGeoEntityMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeolocationServiceImpl implements GeolocationService {

    private final OpenWeatherApiClient openWeatherApiClient;
    private final LocationService locationService;
    private final LocationGeoEntityMapper mapper;

    @Override
    public LocationGeoDto getGeolocationByIsoCodeAndName(
            final String countryIsoCode, final String nameOfLocation) {
        Optional<Location> optLocation = locationService
                .getLocation(countryIsoCode, nameOfLocation);

        return optLocation.map(mapper::toDto)
                .orElseGet(() ->
                        fetchAndStoreGeolocation(
                                countryIsoCode, nameOfLocation)
                );
    }

    private LocationGeoDto fetchAndStoreGeolocation(
            final String countryIsoCode, final String nameOfLocation) {
        LocationGeoDto locationGeoDto = (LocationGeoDto) openWeatherApiClient
                .executeRequestFromStrategy(
                        countryIsoCode, nameOfLocation, Strategies.GEOLOCATION
                );
        return convertAndPersistLocation(locationGeoDto);
    }

    private LocationGeoDto convertAndPersistLocation(
            final LocationGeoDto locationGeoDto) {
        Location location = mapper.toEntity(locationGeoDto);
        locationService.create(location);

        return locationGeoDto;
    }
}
