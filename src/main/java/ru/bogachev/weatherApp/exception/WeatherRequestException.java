package ru.bogachev.weatherApp.exception;

public class WeatherRequestException extends RuntimeException {

    public WeatherRequestException(final String message) {
        super(message);
    }

    public WeatherRequestException(final String message,
                                   final Throwable cause) {
        super(message, cause);
    }
}
