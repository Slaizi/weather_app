package ru.bogachev.weatherApp.integration.openweather;

import ru.bogachev.weatherApp.dto.location.LocationDto;

public interface OpenWeatherApi {

    LocationDto performRequestBasedOnStrategy(
            String countryIsoCode, String nameOfLocation,
            Strategies strategies
    );

}
