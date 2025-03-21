package ru.bogachev.weatherApp.support.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;
import ru.bogachev.weatherApp.model.location.Location;
import ru.bogachev.weatherApp.model.location.LocationNames;

import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationGeoEntityMapper
        extends Mappable<Location, LocationGeoDto> {

    @Override
    @Mapping(target = "cityName", source = "name")
    @Mapping(target = "localNames", source = "localNames",
            qualifiedByName = "mapToLocationNames")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Location toEntity(LocationGeoDto dto);

    @Override
    @Mapping(target = "name", source = "cityName")
    @Mapping(target = "localNames", source = "localNames",
            qualifiedByName = "mapToMap")
    LocationGeoDto toDto(Location entity);

    @Named("mapToLocationNames")
    static LocationNames mapToLocationNames(Map<String, String> localNames) {
        return localNames != null ? new LocationNames(localNames) : null;
    }

    @Named("mapToMap")
    static Map<String, String> mapToMap(LocationNames locationNames) {
        return locationNames != null ? locationNames.getLocalNames() : null;
    }
}
