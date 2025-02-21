package ru.bogachev.weatherApp.exception;

public class GeoRequestException extends RuntimeException {

    public GeoRequestException(final String message) {
        super(message);
    }

}
