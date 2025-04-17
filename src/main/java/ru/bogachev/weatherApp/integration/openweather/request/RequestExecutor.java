package ru.bogachev.weatherApp.integration.openweather.request;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.bogachev.weatherApp.dto.location.LocationDto;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class RequestExecutor implements RequestWorker {

    protected abstract LocationDto mapResponse(
            Response response) throws IOException;

    protected abstract RuntimeException getExceptionBecauseBadRequest(
            Response response);

    protected abstract RuntimeException getExceptionForReadingRecord();

    private final OkHttpClient client;

    public final LocationDto executeRequest(final Request request) {
        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && Objects.nonNull(response.body())) {
                return mapResponse(response);
            }

            throw getExceptionBecauseBadRequest(response);
        } catch (IOException e) {
            throw getExceptionForReadingRecord();
        }

    }
}
