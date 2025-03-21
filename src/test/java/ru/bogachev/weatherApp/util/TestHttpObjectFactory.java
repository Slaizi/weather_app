package ru.bogachev.weatherApp.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestHttpObjectFactory {

    public static @NotNull Request createRequestWithGetMethod(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    @Contract("_ -> new")
    public static @NotNull ResponseBody createResponseBody(String jsonResponse) {
        return ResponseBody
                .create(jsonResponse, MediaType.get("application/json"));
    }

    public static @NotNull Response createResponse(Request request,
                                                   @NotNull HttpStatusCode code,
                                                   String message,
                                                   ResponseBody responseBody) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code.value())
                .message(message)
                .body(responseBody)
                .build();
    }


}
