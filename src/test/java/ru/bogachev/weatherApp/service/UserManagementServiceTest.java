package ru.bogachev.weatherApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.exception.UserAlreadyExistsException;
import ru.bogachev.weatherApp.exception.UserNotFoundException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.repository.UserRepository;
import ru.bogachev.weatherApp.service.impl.UserManagementServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    private static final String DEFAULT_EMAIL = "user@example.com";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManagementServiceImpl userService;

    @Test
    void create_withNoExistsUser_saveSuccessfully() {
        User user = createUser();

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);

        userService.create(user);

        assertEquals(DEFAULT_EMAIL, user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void create_withExistUser_throwException() {
        User user = createUser();
        String expectedMessage = String.format(
                "Пользователь с адресом электронной почты "
                + "'%s' уже существует. Проверьте введённые данные.",
                DEFAULT_EMAIL
        );

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        UserAlreadyExistsException exception = assertThrowsExactly(UserAlreadyExistsException.class,
                () -> userService.create(user));

        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void getByEmail_withExistUser_getSuccessfully() {
        String email = DEFAULT_EMAIL;
        User user = createUser();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        User userResult = userService.getByEmail(email);

        assertNotNull(userResult);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getByEmail_withUserNotFound_throwException() {
        String email = DEFAULT_EMAIL;
        String expectedMessage = String.format(
                "Пользователь с адресом электронной почты "
                + "'%s' не был найден. Проверьте введённые данные.",
                email
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrowsExactly(UserNotFoundException.class,
                () -> userService.getByEmail(email));

        assertEquals(expectedMessage, exception.getMessage());
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .email(DEFAULT_EMAIL)
                .password(ENCODED_PASSWORD)
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }
}
