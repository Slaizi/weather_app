package ru.bogachev.weatherApp.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bogachev.weatherApp.exception.UserAlreadyExistsException;
import ru.bogachev.weatherApp.exception.UserNotFoundException;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.repository.UserRepository;
import ru.bogachev.weatherApp.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(@NonNull final User user) {
        String email = user.getEmail();

        boolean exists = userRepository.existsByEmail(email);

        if (Boolean.TRUE.equals(exists)) {
            throw new UserAlreadyExistsException(
                    String.format(
                            "Пользователь с адресом электронной почты "
                            + "'%s' уже существует. "
                            + "Проверьте введённые данные.",
                            email
                    )
            );
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(@NonNull final String email) {
        Optional<User> optional = userRepository.findByEmail(email);

        boolean present = optional.isPresent();

        if (Boolean.FALSE.equals(present)) {
            throw new UserNotFoundException(
                    String.format(
                            "Пользователь с адресом электронной почты "
                            + "'%s' не был найден. Проверьте введённые данные.",
                            email
                    )
            );
        }

        return optional.get();
    }
}
