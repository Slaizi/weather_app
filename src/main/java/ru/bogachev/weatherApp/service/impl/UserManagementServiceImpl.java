package ru.bogachev.weatherApp.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bogachev.weatherApp.exception.UserNotFoundException;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.repository.UserRepository;
import ru.bogachev.weatherApp.service.UserManagementService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void create(@NonNull final User user) {
        String email = user.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь с адресом "
                                  + "электронной почты '%s' "
                                  + "уже существует. "
                                  + "Проверьте введённые данные.",
                            email)
            );

        }
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(@NonNull final String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElseThrow(() ->
                new UserNotFoundException(
                        String.format("Пользователь с адресом "
                                      + "электронной почты '%s' "
                                      + "не был найден. "
                                      + "Проверьте введённые данные",
                                email)
                )
        );
    }
}
