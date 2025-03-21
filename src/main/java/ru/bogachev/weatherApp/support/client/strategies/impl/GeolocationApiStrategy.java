package ru.bogachev.weatherApp.support.client.strategies.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.exception.GeoNotFoundException;
import ru.bogachev.weatherApp.exception.GeoRequestException;
import ru.bogachev.weatherApp.support.builder.HttpRequestBuilder;
import ru.bogachev.weatherApp.support.client.strategies.ApiClientStrategy;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GeolocationApiStrategy implements ApiClientStrategy {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final HttpRequestBuilder httpRequestBuilder;

    @Override
    public LocationDto executeRequest(final String countryIsoCode,
                                      final String nameOfLocation) {
        Request request = httpRequestBuilder
                .buildGeocodingRequest(countryIsoCode, nameOfLocation);
        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && Objects.nonNull(response.body())) {
                return mapResponse(response);
            }

            throw new GeoRequestException(
                    String.format(
                            "Запрос на получение геокодирования "
                            + "не удался. Статус: %d (%s)",
                            response.code(), response.message())
            );
        } catch (IOException e) {
            throw new GeoRequestException(
                    "Ошибка при выполнении запроса "
                    + "геокодирования: проблема с чтением/записью данных."
            );
        }
    }

    private LocationDto mapResponse(final @NotNull Response response)
            throws IOException {
        try (ResponseBody responseBody = response.body()) {
            List<LocationGeoDto> geoLocations = objectMapper
                    .readValue(Objects.requireNonNull(responseBody)
                            .string(), new TypeReference<>() {
                    });
            return getFirstLocationByList(geoLocations);
        }
    }

    private LocationDto getFirstLocationByList(
            final @NotNull List<LocationGeoDto> geoLocations) {
        if (geoLocations.isEmpty()) {
            throw new GeoNotFoundException(
                    "Местоположение не было найдено, "
                    + "проверьте корректность параметров запроса.");
        }
        return geoLocations.get(0);
    }
}
