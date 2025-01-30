package ru.bogachev.weatherApp.dto.location.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {

    @JsonProperty(value = "temp")
    private Double temperature;

    @JsonProperty(value = "feels_like")
    private Double temperatureFeelsLike;

    @JsonProperty(value = "temp_min")
    private Double temperatureMin;

    @JsonProperty(value = "temp_max")
    private Double temperatureMax;

    @JsonProperty(value = "pressure")
    private Integer pressure;

    @JsonProperty(value = "humidity")
    private Integer humidity;

}
