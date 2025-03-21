package ru.bogachev.weatherApp.support.client.strategies.impl;

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
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.exception.WeatherRequestException;
import ru.bogachev.weatherApp.service.GeolocationService;
import ru.bogachev.weatherApp.support.builder.HttpRequestBuilder;
import ru.bogachev.weatherApp.support.client.strategies.ApiClientStrategy;
import ru.bogachev.weatherApp.support.mapper.LocationGeoEntityMapper;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WeatherForDayApiStrategy implements ApiClientStrategy {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final HttpRequestBuilder httpRequestBuilder;
    private final GeolocationService geolocationService;
    private final LocationGeoEntityMapper mapper;

    @Override
    public LocationDto executeRequest(final String countryIsoCode,
                                      final String nameOfLocation) {
        Request request = getRequest(countryIsoCode, nameOfLocation);

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && Objects.nonNull(response.body())) {
                return mapResponse(response);
            }

            throw new WeatherRequestException(
                    String.format(
                            "Запрос на получение погоды "
                            + "не удался. Статус: %d (%s)",
                            response.code(), response.message()
                    )
            );
        } catch (IOException e) {
            throw new WeatherRequestException(
                    "Ошибка при выполнении запроса "
                    + "на получение погоды: проблема с чтением/записью данных."
            );
        }
    }

    private Request getRequest(final String countryIsoCode,
                               final String nameOfLocation) {
        LocationGeoDto dto = geolocationService
                .getGeolocationByIsoCodeAndName(countryIsoCode, nameOfLocation);
        return httpRequestBuilder.buildWeatherCityRequest(mapper.toEntity(dto));
    }

    private LocationDto mapResponse(final @NotNull Response response)
            throws IOException {
        try (ResponseBody responseBody = response.body()) {
            return objectMapper.readValue(
                    Objects.requireNonNull(responseBody)
                            .string(), LocationWeatherDto.class
            );
        }
    }
}
