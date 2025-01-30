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
public class Weather {

    @JsonProperty(value = "id")
    private Integer id;

    @JsonProperty(value = "main")
    private String currentState;

    @JsonProperty(value = "description")
    private String description;

}
