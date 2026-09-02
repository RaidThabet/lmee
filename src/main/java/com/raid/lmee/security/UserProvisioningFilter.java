package com.raid.lmee.security;

import java.io.IOException;
import java.util.UUID;

import com.raid.lmee.domain.User;
import com.raid.lmee.repos.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Creates the local {@code users} row for a Keycloak subject on its first authenticated
 * request. Keycloak owns the identity; this row only anchors application data such as
 * favourite clubs.
 * <p>
 * Runs after {@code BearerTokenAuthenticationFilter}, so the JWT is already validated.
 */
@Component
@RequiredArgsConstructor
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt jwt) {
            provision(jwt);
        }

        filterChain.doFilter(request, response);
    }

    private void provision(Jwt jwt) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());

        if (userRepository.existsById(keycloakId)) {
            return;
        }

        User user = new User();
        user.setKeycloakId(keycloakId);

        try {
            userRepository.save(user);
        }
        catch (DataIntegrityViolationException ex) {
            logger.debug("User %s was provisioned concurrently".formatted(keycloakId), ex);
        }
    }

}
