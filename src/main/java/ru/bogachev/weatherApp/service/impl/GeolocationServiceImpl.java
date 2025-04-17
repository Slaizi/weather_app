package ru.bogachev.weatherApp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.GeolocationService;
import ru.bogachev.weatherApp.service.LocationService;
import ru.bogachev.weatherApp.integration.openweather.OpenWeatherApi;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.support.mapper.LocationGeoEntityMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeolocationServiceImpl implements GeolocationService {

    private final OpenWeatherApi openWeatherApi;
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
        LocationGeoDto locationGeoDto = (LocationGeoDto) openWeatherApi
                .performRequestBasedOnStrategy(
                        countryIsoCode, nameOfLocation,
                        Strategies.GET_GEOLOCATION
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
