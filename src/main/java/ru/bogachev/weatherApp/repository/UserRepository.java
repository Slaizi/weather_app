package ru.bogachev.weatherApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bogachev.weatherApp.model.user.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)
            """, nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
