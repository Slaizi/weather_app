package ru.bogachev.weatherApp.dto.location.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wind {

    @JsonProperty(value = "speed")
    private Double speed;

    @JsonProperty(value = "deg")
    private Integer deg;

}
