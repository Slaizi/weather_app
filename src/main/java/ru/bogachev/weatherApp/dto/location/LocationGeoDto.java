package ru.bogachev.weatherApp.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationGeoDto {

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "local_names")
    private Map<String, String> localNames;

    @JsonProperty(value = "lat")
    private Double latitude;

    @JsonProperty(value = "lon")
    private Double longitude;

    @JsonProperty(value = "country")
    private String country;

}
