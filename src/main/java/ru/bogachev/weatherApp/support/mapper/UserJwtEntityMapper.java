package ru.bogachev.weatherApp.support.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.bogachev.weatherApp.security.JwtUserDetails;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserJwtEntityMapper extends Mappable<User, JwtUserDetails> {

    @Override
    @Mapping(target = "username", source = "email")
    @Mapping(target = "authorities", source = "roles",
            qualifiedByName = "mapToGrantedAuthority")
    JwtUserDetails toDto(User entity);

    @Named("mapToGrantedAuthority")
    default Set<GrantedAuthority> mapToGrantedAuthority(final Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
