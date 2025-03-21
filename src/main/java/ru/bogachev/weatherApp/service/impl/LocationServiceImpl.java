package ru.bogachev.weatherApp.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.repository.LocationRepository;
import ru.bogachev.weatherApp.service.LocationService;
import ru.bogachev.weatherApp.support.helper.NameHelper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public Location create(@NotNull final Location location) {
        String country = location.getCountry();
        String cityName = location.getCityName();

        boolean exists = locationRepository
                .existsByCountryAndCity(country, cityName);

        if (Boolean.TRUE.equals(exists)) {
            return location;
        }

        return locationRepository.save(location);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Location> getLocation(final String countyIsoCode,
                                          final String nameOfLocation) {
        String correctNameLocation = NameHelper
                .getCorrectNameOfLocation(nameOfLocation);

        return locationRepository.findLocationByJsonbValue(
                countyIsoCode, correctNameLocation);
    }
}
