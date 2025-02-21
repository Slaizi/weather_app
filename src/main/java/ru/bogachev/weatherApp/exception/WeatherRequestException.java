package ru.bogachev.weatherApp.exception;

public class WeatherRequestException extends RuntimeException {

    public WeatherRequestException(final String message) {
        super(message);
    }

}
