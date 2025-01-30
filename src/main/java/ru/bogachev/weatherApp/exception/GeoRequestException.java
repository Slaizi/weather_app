package ru.bogachev.weatherApp.exception;

public class GeoRequestException extends RuntimeException {

    public GeoRequestException(final String message) {
        super(message);
    }

    public GeoRequestException(final String message,
                               final Throwable cause) {
        super(message, cause);
    }
}
