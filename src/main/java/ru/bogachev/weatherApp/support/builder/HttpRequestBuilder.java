package ru.bogachev.weatherApp.support.builder;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.model.location.Location;

@Component
@RequiredArgsConstructor
public class HttpRequestBuilder {

    private final WeatherProperties weatherProperties;

    public Request buildGeocodingRequest(
            final String countryIsoCode,
            final String nameOfLocation
    ) {
        String url = buildUrlForGeocodingRequest(
                countryIsoCode, nameOfLocation);

        return buildGetRequest(url);
    }

    private @NotNull String buildUrlForGeocodingRequest(
            final String countryIsoCode,
            final String nameOfLocation
    ) {
        WeatherProperties.Url url = weatherProperties.getUrl();
        String apiKey = weatherProperties.getApiKey();

        return url.getBasicPath()
               + url.getGeocodingSuffix()
               + "?q=" + nameOfLocation
               + ", " + countryIsoCode
               + "&limit=1"
               + "&appid=" + apiKey;
    }

    public Request buildWeatherCityRequest(
            final Location location
    ) {
        String url = buildUrlForWeatherCityRequest(location);

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

    private @NotNull Request buildGetRequest(
            final String url
    ) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

}
