package ru.bogachev.weatherApp.support.client.strategies;

import ru.bogachev.weatherApp.dto.location.LocationDto;

public interface ApiClientStrategy {

    LocationDto executeRequest(String countryIsoCode, String nameOfLocation);

}
