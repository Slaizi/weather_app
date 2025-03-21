package ru.bogachev.weatherApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bogachev.weatherApp.model.location.Location;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = """
            SELECT *
            FROM locations
            WHERE country ILIKE :country
              AND EXISTS (
                SELECT 1
                FROM jsonb_each_text(local_names -> 'local_names')
                WHERE value = :value
            )
            LIMIT 1
            """, nativeQuery = true)
    Optional<Location> findLocationByJsonbValue(
            @Param("country") String countryIsoCode,
            @Param("value") String localName);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM locations
                WHERE country ILIKE :country
                AND city_name = :cityName
            )
            """, nativeQuery = true)
    boolean existsByCountryAndCity(
            @Param("country") String countryIsoCode,
            @Param("cityName") String cityName
    );
}
