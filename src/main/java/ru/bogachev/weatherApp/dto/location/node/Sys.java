package ru.bogachev.weatherApp.dto.location.node;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bogachev.weatherApp.support.deserializer.UnixTimestampDeserializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sys implements Serializable {

    @JsonProperty(value = "sunrise")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime sunrise;

    @JsonProperty(value = "sunset")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime sunset;
}
