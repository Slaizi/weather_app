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
public class Clouds {

    @JsonProperty(value = "all")
    private Integer all;
}
