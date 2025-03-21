package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.dto.location.LocationGeoDto;

public interface GeolocationService {

    LocationGeoDto getGeolocationByIsoCodeAndName(String countryIsoCode,
                                                  String nameOfLocation);

}
