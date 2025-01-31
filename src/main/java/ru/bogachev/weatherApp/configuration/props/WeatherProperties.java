package ru.bogachev.weatherApp.configuration.props;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "open-weather")
public class WeatherProperties {

    private Url url;
    private String apiKey;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Url {

        private String basicPath;
        private String weatherSuffix;
        private String geocodingSuffix;

    }
}
