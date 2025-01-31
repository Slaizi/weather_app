package ru.bogachev.weatherApp.dto.location;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bogachev.weatherApp.dto.location.node.*;
import ru.bogachev.weatherApp.support.deserializer.UnixTimestampDeserializer;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationWeatherDto {

    @JsonProperty(value = "weather")
    private List<Weather> weathers;

    @JsonProperty(value = "main")
    private Main main;

    @JsonProperty(value = "wind")
    private Wind wind;

    @JsonProperty(value = "clouds")
    private Clouds clouds;

    @JsonProperty(value = "dt")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime dateTime;

    @JsonProperty(value = "sys")
    private Sys sys;

}
