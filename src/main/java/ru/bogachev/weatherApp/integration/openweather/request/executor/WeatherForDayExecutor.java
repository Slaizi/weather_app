package ru.bogachev.weatherApp.integration.openweather.request.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.dto.location.LocationWeatherDto;
import ru.bogachev.weatherApp.exception.WeatherRequestException;
import ru.bogachev.weatherApp.integration.openweather.Strategies;
import ru.bogachev.weatherApp.integration.openweather.request.RequestExecutor;

import java.io.IOException;
import java.util.Objects;

import static ru.bogachev.weatherApp.integration.openweather.Strategies.GET_WEATHER_FOR_DAY;

@Component
public class WeatherForDayExecutor extends RequestExecutor {

    private final ObjectMapper objectMapper;

    @Autowired
    public WeatherForDayExecutor(
            final OkHttpClient client,
            final ObjectMapper objectMapper) {
        super(client);
        this.objectMapper = objectMapper;
    }

    @Override
    public Strategies getStrategy() {
        return GET_WEATHER_FOR_DAY;
    }

    @Override
    protected LocationDto mapResponse(final @NotNull Response response)
            throws IOException {

        try (ResponseBody responseBody = response.body()) {
            return objectMapper
                    .readValue(Objects.requireNonNull(responseBody).bytes(),
                            LocationWeatherDto.class);
        }
    }

    @Override
    protected RuntimeException getExceptionBecauseBadRequest(
            final @NotNull Response response) {
        return new WeatherRequestException(
                String.format(
                        "Запрос на получение погоды "
                        + "не удался. Статус: %d (%s)",
                        response.code(), response.message()
                )
        );
    }

    @Override
    protected RuntimeException getExceptionForReadingRecord() {
        return new WeatherRequestException(
                "Ошибка при выполнении запроса "
                + "на получение погоды: проблема с чтением/записью данных."
        );
    }
}
