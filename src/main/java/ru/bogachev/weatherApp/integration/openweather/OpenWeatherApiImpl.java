package ru.bogachev.weatherApp.integration.openweather;

import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.integration.openweather.request.RequestBuilder;
import ru.bogachev.weatherApp.integration.openweather.request.RequestExecutor;
import ru.bogachev.weatherApp.integration.openweather.request.RequestWorker;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OpenWeatherApiImpl implements OpenWeatherApi {

    private final Map<Strategies, RequestBuilder> requestBuilderMap;
    private final Map<Strategies, RequestExecutor> requestExecutorMap;

    @Autowired
    public OpenWeatherApiImpl(final List<RequestBuilder> builders,
                              final List<RequestExecutor> executors) {
        this.requestBuilderMap = putMap(builders);
        this.requestExecutorMap = putMap(executors);
    }

    private <T extends RequestWorker> Map<Strategies, T> putMap(
            final @NotNull List<T> workers) {
        return workers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        RequestWorker::getStrategy,
                        Function.identity()
                ));
    }

    @Override
    public LocationDto performRequestBasedOnStrategy(
            final String countyIsoCode,
            final String nameOfLocation,
            final Strategies strategies
    ) {
        if (!requestBuilderMap.containsKey(strategies)
            || !requestExecutorMap.containsKey(strategies)) {
            throw new IllegalArgumentException();
        }
        RequestBuilder requestBuilder = requestBuilderMap
                .get(strategies);
        Request request = requestBuilder
                .buildRequest(countyIsoCode, nameOfLocation);
        RequestExecutor requestExecutor = requestExecutorMap.get(strategies);
        return requestExecutor.executeRequest(request);
    }
}
