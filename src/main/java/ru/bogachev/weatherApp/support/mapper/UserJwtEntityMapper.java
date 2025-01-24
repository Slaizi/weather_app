package ru.bogachev.weatherApp.support.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.bogachev.weatherApp.model.user.Role;
import ru.bogachev.weatherApp.model.user.User;
import ru.bogachev.weatherApp.security.JwtUserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserJwtEntityMapper extends Mappable<User, JwtUserDetails> {

    @Override
    @Mapping(target = "username", source = "email")
    @Mapping(target = "authorities", source = "roles",
            qualifiedByName = "mapToGrantedAuthority")
    JwtUserDetails toDto(User entity);

    @Override
    @Mapping(target = "email", source = "username")
    @Mapping(target = "roles", source = "authorities",
            qualifiedByName = "mapToUserSetRoles")
    User toEntity(JwtUserDetails dto);

    @Named("mapToGrantedAuthority")
    default Set<GrantedAuthority> mapToGrantedAuthority(final Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Named("mapToUserSetRoles")
    default Set<Role> mapToUserSetRoles(
            final Collection<? extends GrantedAuthority> authorities
    ) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
