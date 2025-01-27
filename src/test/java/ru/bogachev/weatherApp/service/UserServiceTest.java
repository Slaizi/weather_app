package ru.bogachev.weatherApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bogachev.weatherApp.exception.UserNotFoundException;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.repository.UserRepository;
import ru.bogachev.weatherApp.service.impl.UserServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String DEFAULT_EMAIL = "user@example.com";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldSaveUserWhenEmailDoesNotExist() {
        User user = createUser();

        when(userRepository.existsByEmail(DEFAULT_EMAIL))
                .thenReturn(false);

        userService.create(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(user, userCaptor.getValue());
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        String email = DEFAULT_EMAIL;
        User user = createUser();

        when(userRepository.existsByEmail(email))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrowsExactly(IllegalArgumentException.class,
                () -> userService.create(user));

        assertEquals(
                String.format("Пользователь с адресом электронной почты "
                              + "'%s' уже существует. "
                              + "Проверьте введённые данные.", email),
                exception.getMessage()
        );
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldReturnUserByEmail() {
        String email = DEFAULT_EMAIL;
        User user = createUser();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        User actualUser = userService.getByEmail(email);

        assertNotNull(actualUser);
        assertEquals(user, actualUser);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionIfUserNotFoundByEmail() {
        String email = DEFAULT_EMAIL;

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrowsExactly(UserNotFoundException.class,
                () -> userService.getByEmail(email));

        assertEquals(String.format(
                        "Пользователь с адресом "
                        + "электронной почты '%s' "
                        + "не был найден. "
                        + "Проверьте введённые данные", email),
                exception.getMessage()
        );
        verify(userRepository).findByEmail(email);
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
