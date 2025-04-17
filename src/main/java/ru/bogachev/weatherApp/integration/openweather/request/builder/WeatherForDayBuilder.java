package ru.bogachev.weatherApp.integration.openweather.request.builder;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.integration.openweather.request.RequestBuilder;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.GeolocationService;
import ru.bogachev.weatherApp.support.mapper.LocationGeoEntityMapper;

import static ru.bogachev.weatherApp.integration.openweather.Strategies.GET_WEATHER_FOR_DAY;

@Component
@RequiredArgsConstructor
public class WeatherForDayBuilder implements RequestBuilder {

    private final GeolocationService geolocationService;
    private final LocationGeoEntityMapper mapper;
    private final WeatherProperties weatherProperties;

    @Override
    public Strategies getStrategy() {
        return GET_WEATHER_FOR_DAY;
    }

    @Override
    public Request buildRequest(final String countryIsoCode,
                                final String nameOfLocation) {
        LocationGeoDto geolocation = geolocationService
                .getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation);
        String url = buildUrlForWeatherCityRequest(
                mapper.toEntity(geolocation)
        );

        return buildGetRequest(url);
    }

    private @NotNull String buildUrlForWeatherCityRequest(
            final @NotNull Location location
    ) {
        WeatherProperties.Url url = weatherProperties.getUrl();
        Double lon = location.getLongitude();
        Double lat = location.getLatitude();
        String countryIsoCode = location.getCountry();
        String apiKey = weatherProperties.getApiKey();

        return url.getBasicPath()
               + url.getWeatherSuffix()
               + "?lat=" + lat
               + "&lon=" + lon
               + "&lang=" + countryIsoCode
               + "&units=metric"
               + "&appid=" + apiKey;
    }
}
