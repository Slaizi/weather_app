package ru.bogachev.weatherApp.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Ответ, содержащий информацию о местоположении, используемый для получения погоды.")
public class LocationGeoDto implements LocationDto {

    @Schema(description = "Название города.", example = "Moscow")
    @JsonProperty(value = "name")
    private String name;

    @Schema(description = "Названия города на разных языках, где ключ — код языка (ISO 639-1), а значение — название города на этом языке.",
            example = "{\"ru\": \"Москва\", \"en\": \"Moscow\", \"fi\": \"Moskova\", \"fo\": \"Moskva\"}")
    @JsonProperty(value = "local_names")
    private Map<String, String> localNames;

    @Schema(description = "Широта.", example = "55.7504461")
    @JsonProperty(value = "lat")
    private Double latitude;

    @Schema(description = "Долгота.", example = "37.6174943")
    @JsonProperty(value = "lon")
    private Double longitude;

    @Schema(description = "Страна расположения.", example = "ru")
    @JsonProperty(value = "country")
    private String country;

}
