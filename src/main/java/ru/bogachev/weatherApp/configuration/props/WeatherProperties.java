package ru.bogachev.weatherApp.configuration.props;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "open_weather")
public class WeatherProperties {

    private final Url url;
    private final String apiKey;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Url {

        private final String basicPath;
        private final String weatherSuffix;
        private final String geocodingSuffix;

    }
}
