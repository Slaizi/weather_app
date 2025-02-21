package ru.bogachev.weatherApp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.exception.GeoRequestException;
import ru.bogachev.weatherApp.exception.WeatherRequestException;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.service.WeatherDataService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherDataServiceImpl implements WeatherDataService {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final WeatherProperties weatherProperties;

    @Override
    public LocationGeoDto getLocationGeoByName(
            final String countyIsoCode,
            final String nameOfLocation) {
        Request request = buildGeocodingRequest(countyIsoCode, nameOfLocation);
        List<LocationGeoDto> locations = executeGeoRequest(request);

        return getFirstLocationOrDefault(locations);
    }

    private @NotNull Request buildGeocodingRequest(
            final String countyIsoCode,
            final String nameOfLocation) {
        String url = buildUrlForGeocodingRequest(
                countyIsoCode, nameOfLocation);
        return buildGetRequest(url);
    }

    private @NotNull String buildUrlForGeocodingRequest(
            final String countyIsoCode,
            final String nameOfLocation) {

        WeatherProperties.Url url = weatherProperties.getUrl();
        String apiKey = weatherProperties.getApiKey();

        return url.getBasicPath()
               + url.getGeocodingSuffix()
               + "?q=" + nameOfLocation
               + ", " + countyIsoCode
               + "&limit=1"
               + "&appid=" + apiKey;
    }

    private List<LocationGeoDto> executeGeoRequest(
            final Request request) {
        try (Response response = client.newCall(request).execute()) {
            return processGeoResponse(response);
        } catch (IOException e) {
            throw new GeoRequestException(
                    "Error while executing geocoding request"
            );
        }
    }

    @SneakyThrows
    private List<LocationGeoDto> processGeoResponse(
            final @NotNull Response response) {
        if (response.isSuccessful() && response.body() != null) {
            return objectMapper.readValue(response.body().string().
                    getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
            });
        }
        return List.of();
    }

    private LocationGeoDto getFirstLocationOrDefault(
            final @NotNull List<LocationGeoDto> locations) {
        return locations.isEmpty() ? new LocationGeoDto() : locations.get(0);
    }

    @Override
    public LocationWeatherDto getWeatherForLocation(
            final Location location) {
        Request request = buildWeatherRequest(location);
        return executeWeatherRequest(request);
    }

    private @NotNull Request buildWeatherRequest(
            final Location location) {
        String url = buildUrlForWeatherRequest(location);
        return buildGetRequest(url);
    }

    private @NotNull String buildUrlForWeatherRequest(
            final @NotNull Location location) {

        WeatherProperties.Url url = weatherProperties.getUrl();
        Double lat = location.getLatitude();
        Double lon = location.getLongitude();
        String countryCode = location.getCountry();
        String apiKey = weatherProperties.getApiKey();

        return url.getBasicPath()
               + url.getWeatherSuffix()
               + "?lat=" + lat
               + "&lon=" + lon
               + "&lang=" + countryCode
               + "&units=metric"
               + "&appid=" + apiKey;
    }

    private LocationWeatherDto executeWeatherRequest(
            final Request request) {
        try (Response response = client.newCall(request).execute()) {
            return processWeatherResponse(response);
        } catch (IOException e) {
            throw new WeatherRequestException(
                    "Error while executing weather request"
            );
        }
    }

    @SneakyThrows
    private LocationWeatherDto processWeatherResponse(
            final @NotNull Response response) {
        if (response.isSuccessful() && response.body() != null) {
            return objectMapper.readValue(response.body().string()
                    .getBytes(StandardCharsets.UTF_8),
                    LocationWeatherDto.class
            );
        }
        return new LocationWeatherDto();
    }

    private @NotNull Request buildGetRequest(final String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

}
