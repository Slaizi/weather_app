package ru.bogachev.weatherApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.exception.UserNotFoundException;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.repository.UserRepository;
import ru.bogachev.weatherApp.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserSuccessTest() {
        User user = User.builder().email("user@example.com").build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        userService.create(user);

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void createUserWithDuplicateEmailThrowsExceptionTest() {
        User user = User.builder().email("user@example.com").build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.create(user)
        );

        assertEquals(
                "Пользователь с адресом электронной почты "
                + "'user@example.com' уже существует. "
                + "Проверьте введённые данные.",
                exception.getMessage()
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getByEmailTest() {
        String email = "user@example.com";
        User expectedUser = User.builder().id(1L).build();

        Optional<User> optionalUser = Optional.of(expectedUser);

        when(userRepository.findByEmail(email)).thenReturn(optionalUser);

        User actualUser = userService.getByEmail(email);

        verify(userRepository).findByEmail(email);

        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getByEmailWithUserNotFoundExceptionTest() {
        String email = "user@example.com";
        Optional<User> optionalUser = Optional.empty();

        when(userRepository.findByEmail(email)).thenReturn(optionalUser);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getByEmail(email)
        );

        verify(userRepository).findByEmail(email);

        assertEquals("Пользователь с адресом "
                     + "электронной почты 'user@example.com' "
                     + "не был найден. "
                     + "Проверьте введённые данные",
                exception.getMessage());
    }
}
