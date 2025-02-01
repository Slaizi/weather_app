package ru.bogachev.weatherApp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.service.UserManagementService;
import ru.bogachev.weatherApp.support.mapper.UserJwtEntityMapper;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserManagementService userManagementService;
    private final UserJwtEntityMapper userJwtEntityMapper;

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        User user = userManagementService.getByEmail(username);
        return userJwtEntityMapper.toDto(user);
    }
}
