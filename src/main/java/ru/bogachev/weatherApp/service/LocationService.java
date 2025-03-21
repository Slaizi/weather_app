package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.model.location.Location;

import java.util.Optional;

public interface LocationService {

    Location create(Location location);

    Optional<Location> getLocation(String countyIsoCode, String nameOfLocation);

}
