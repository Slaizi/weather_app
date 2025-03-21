package ru.bogachev.weatherApp.support.client.strategies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.bogachev.weatherApp.support.client.strategies.impl.GeolocationApiStrategy;
import ru.bogachev.weatherApp.support.client.strategies.impl.WeatherForDayApiStrategy;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
public enum Strategies {

    GEOLOCATION(GeolocationApiStrategy.class),
    WEATHER_FOR_DAY(WeatherForDayApiStrategy.class);

    private final Class<? extends ApiClientStrategy> strategyClass;

    private static final Map<Class<? extends ApiClientStrategy>,
            Strategies> STRATEGY_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(
                    Strategies::getStrategyClass,
                    Function.identity())
            );

    public static Strategies getStrategyByClass(
            final Class<? extends ApiClientStrategy> clazz) {
        return STRATEGY_MAP.get(clazz);
    }
}
