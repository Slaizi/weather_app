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
            WHERE country ILIKE :iso_code
              AND EXISTS (
                SELECT 1
                FROM jsonb_each_text(local_names -> 'local_names')
                WHERE value = :value
            )
            LIMIT 1
            """, nativeQuery = true)
    Optional<Location> findLocationByJsonbValue(
            @Param("iso_code") String isoCode,
            @Param("value") String localName);
}
