package ru.bogachev.weatherApp.integration.openweather.request.executor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.exception.GeoNotFoundException;
import ru.bogachev.weatherApp.exception.GeoRequestException;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.integration.openweather.request.RequestExecutor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ru.bogachev.weatherApp.integration.openweather.Strategies.GET_GEOLOCATION;

@Component
public class GeoByCountryAndNameExecutor extends RequestExecutor {

    private final ObjectMapper objectMapper;

    @Autowired
    public GeoByCountryAndNameExecutor(
            final OkHttpClient client,
            final ObjectMapper objectMapper) {
        super(client);
        this.objectMapper = objectMapper;
    }

    @Override
    public Strategies getStrategy() {
        return GET_GEOLOCATION;
    }

    @Override
    protected LocationDto mapResponse(final @NotNull Response response)
            throws IOException {

        try (ResponseBody responseBody = response.body()) {
            List<LocationGeoDto> geoLocations = objectMapper.readValue(Objects
                            .requireNonNull(responseBody).bytes(),
                    new TypeReference<>() {
                    }
            );
            return getFirstLocationFromList(geoLocations);
        }
    }

    private LocationGeoDto getFirstLocationFromList(
            final @NotNull List<LocationGeoDto> geoLocations) {

        return geoLocations.stream().findFirst()
                .orElseThrow(() -> new GeoNotFoundException(
                        "Местоположение не было найдено, "
                        + "проверьте корректность параметров запроса.")
                );
    }

    @Override
    protected RuntimeException getExceptionBecauseBadRequest(
            final @NotNull Response response) {
        return new GeoRequestException(
                String.format("Запрос на получение геокодирования "
                              + "не удался. Статус: %d (%s)",
                        response.code(), response.message()
                )
        );
    }

    @Override
    protected RuntimeException getExceptionForReadingRecord() {
        return new GeoRequestException(
                "Ошибка при выполнении запроса "
                + "геокодирования: проблема с чтением/записью данных."
        );
    }
}
