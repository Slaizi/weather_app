package ru.bogachev.weatherApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bogachev.weatherApp.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
