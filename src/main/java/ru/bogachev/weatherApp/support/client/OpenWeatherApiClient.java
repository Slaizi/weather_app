package ru.bogachev.weatherApp.support.client;

import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.support.client.strategies.Strategies;

public interface OpenWeatherApiClient {

    LocationDto executeRequestFromStrategy(
            String countryIsoCode, String nameOfLocation,
            Strategies strategies
    );

}
