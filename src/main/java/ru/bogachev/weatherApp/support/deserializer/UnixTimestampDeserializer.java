package ru.bogachev.weatherApp.support.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UnixTimestampDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(
            final @NotNull JsonParser jsonParser,
            final DeserializationContext deserializationContext)
            throws IOException {

        long timestampInSeconds = jsonParser.getLongValue();

        return LocalDateTime.ofInstant(Instant
                        .ofEpochSecond(timestampInSeconds),
                ZoneId.systemDefault()
        );
    }
}
