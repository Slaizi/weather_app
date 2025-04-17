package ru.bogachev.weatherApp.integration.openweather.request.builder;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.integration.openweather.request.RequestBuilder;

import static ru.bogachev.weatherApp.integration.openweather.Strategies.GET_GEOLOCATION;

@Component
@RequiredArgsConstructor
public class GeoByCountryAndNameBuilder implements RequestBuilder {

    private final WeatherProperties weatherProperties;

    @Override
    public Strategies getStrategy() {
        return GET_GEOLOCATION;
    }

    @Override
    public Request buildRequest(final String countryIsoCode,
                                final String nameOfLocation) {
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
}
