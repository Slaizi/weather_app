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
            SELECT * FROM locations WHERE EXISTS(
                 SELECT 1 FROM jsonb_each_text(
                     jsonb_extract_path(local_names, 'local_names')
                 ) WHERE value = :value
            )
            """, nativeQuery = true)
    Optional<Location> findLocationByJsonbValue(
            @Param("value") String localName);
}
