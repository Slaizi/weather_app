package ru.bogachev.weatherApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bogachev.weatherApp.model.location.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM locations
                CROSS JOIN LATERAL jsonb_each_text(locations.local_names) AS kv
                WHERE kv.value = :value
            )
            """, nativeQuery = true)
    boolean existsLocationByLocalName(@Param("value") String localName);

}
