package ru.bogachev.weatherApp.exception;

public class GeoNotFoundException extends RuntimeException {

    public GeoNotFoundException(final String message) {
        super(message);
    }

}
