package com.raid.lmee.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Maps Keycloak realm roles ({@code realm_access.roles}) to Spring Security authorities.
 * <p>
 * Keycloak role names carry no prefix, so {@code ROLE_} is prepended here. The role
 * hierarchy is expanded eagerly, which means a token holding {@code ADMIN} is granted
 * {@code ROLE_ADMIN}, {@code ROLE_OPERATOR} and {@code ROLE_REGISTERED}.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";

    private static final String ROLES_CLAIM = "roles";

    private static final String ROLE_PREFIX = "ROLE_";

    private final RoleHierarchy roleHierarchy;

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        return new JwtAuthenticationToken(jwt, extractAuthorities(jwt));
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);

        if (realmAccess == null || !(realmAccess.get(ROLES_CLAIM) instanceof Collection<?> roles)) {
            return List.of();
        }

        List<GrantedAuthority> granted = roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .toList();

        return roleHierarchy.getReachableGrantedAuthorities(granted);
    }

}
