package ru.bogachev.weatherApp.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationNames implements Serializable {

    @JsonProperty("local_names")
    private Map<String, String> localNames;


}
