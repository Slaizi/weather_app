package ru.bogachev.weatherApp.integration.openweather.request;

import okhttp3.Request;

public interface RequestBuilder extends RequestWorker {

    Request buildRequest(String countryIsoCode, String nameOfLocation);

    default Request buildGetRequest(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

}
