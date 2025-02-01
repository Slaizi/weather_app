package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.model.location.Location;

public interface WeatherDataService {

    LocationGeoDto getLocationGeoByName(String nameOfLocation);

    LocationWeatherDto getWeatherForLocation(Location location);

}
