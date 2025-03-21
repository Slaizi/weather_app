package ru.bogachev.weatherApp.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bogachev.weatherApp.dto.exception.ErrorMessage;
import ru.bogachev.weatherApp.dto.location.LocationGeoDto;

@RequestMapping("/api/v1/geo")
@Tag(name = "Геолокация", description = "Geo API")
public interface GeolocationApiDocs {

    @Operation(summary = "Получение местоположения по названию города.")
    @ApiResponse(
            responseCode = "200",
            description = "Местоположение получено.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LocationGeoDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка/ошибки выполнения запроса.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Пользователь не авторизирован.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Местоположение не было найдено.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorMessage.class)
            )
    )
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LocationGeoDto> getGeoForLocation(
            @RequestParam(
                    name = "country_code",
                    required = false,
                    defaultValue = "ru"
            ) String countryIsoCode,
            @RequestParam(name = "city") String nameOfLocation
    );

}
