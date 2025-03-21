package ru.bogachev.weatherApp.controller.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bogachev.weatherApp.controller.api.GeolocationApiDocs;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.service.GeolocationService;

@RestController
@RequiredArgsConstructor
public class GeolocationController implements GeolocationApiDocs {

    private final GeolocationService geolocationService;

    @Override
    public ResponseEntity<LocationGeoDto> getGeoForLocation(
            final String countryIsoCode,
            final String nameOfLocation) {
        LocationGeoDto response = geolocationService
                .getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation);
        return ResponseEntity.ok(response);
    }
}
