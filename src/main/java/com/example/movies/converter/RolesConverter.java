package com.example.movies.converter;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@SuppressWarnings("All")
public class RolesConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final String ROLE_ = "ROLE_";
    private final String REALM_ACCESS = "realm_access";
    private final String ROLES = "roles";
    private final String PREFERRED_USERNAME = "preferred_username";

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        List<GrantedAuthority> roles = getAccessRoles(source);
        return new JwtAuthenticationToken(source, roles, Objects.requireNonNull(source.getClaimAsString(PREFERRED_USERNAME)));
    }

    private List<GrantedAuthority> getAccessRoles(Jwt source) {
        Map<String, Object> args = source.getClaimAsMap(REALM_ACCESS);
        if (args == null) {
            return Collections.emptyList();
        }

        List<String> roles = (List<String>) args.get(ROLES);
        return refactorRoles(roles);
    }

    private List<GrantedAuthority> refactorRoles(List<String> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_ + role.toUpperCase())).map(GrantedAuthority.class::cast).toList();
    }

}
