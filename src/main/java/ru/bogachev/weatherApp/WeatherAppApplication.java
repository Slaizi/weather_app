package ru.bogachev.weatherApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.bogachev.weatherApp.configuration.props.WeatherProperties;


@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties(value = WeatherProperties.class)
public class WeatherAppApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WeatherAppApplication.class, args);
    }

}
