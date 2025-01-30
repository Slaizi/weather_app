package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.dto.location.LocationGeoDto;

public interface DataWeatherService {

    LocationGeoDto getLocationGeoByName(String nameOfLocation);
}
