package ru.bogachev.weatherApp.support.client;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bogachev.weatherApp.dto.location.LocationDto;
import ru.bogachev.weatherApp.support.client.strategies.ApiClientStrategy;
import ru.bogachev.weatherApp.support.client.strategies.Strategies;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OpenWeatherApiClientImpl implements OpenWeatherApiClient {

    private final Map<Strategies, ApiClientStrategy> strategyMap;

    @Autowired
    public OpenWeatherApiClientImpl(
            final @NotNull List<ApiClientStrategy> strategies) {
        this.strategyMap = Collections.unmodifiableMap(strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> Strategies
                                .getStrategyByClass(
                                        strategy.getClass()),
                        Function.identity()
                )));
    }

    @Override
    public LocationDto executeRequestFromStrategy(final String countyIsoCode,
                                                  final String nameOfLocation,
                                                  final Strategies strategies) {
        var strategy = strategyMap.get(strategies);
        return strategy.executeRequest(countyIsoCode, nameOfLocation);
    }
}
