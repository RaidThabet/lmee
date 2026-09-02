package com.raid.lmee.config;

import com.raid.lmee.security.JwtAuthConverter;
import com.raid.lmee.security.Roles;
import com.raid.lmee.security.UserProvisioningFilter;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponseAccessDeniedHandler;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.UnauthorizedEntryPoint;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorCodeMapper;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorMessageMapper;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.HttpStatusMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public AccessDeniedHandler accessDeniedHandler(HttpStatusMapper httpStatusMapper,
                                                   ErrorCodeMapper errorCodeMapper,
                                                   ErrorMessageMapper errorMessageMapper,
                                                   ObjectMapper objectMapper) {
        return new ApiErrorResponseAccessDeniedHandler(objectMapper, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public UnauthorizedEntryPoint unauthorizedEntryPoint(HttpStatusMapper httpStatusMapper,
                                                         ErrorCodeMapper errorCodeMapper,
                                                         ErrorMessageMapper errorMessageMapper,
                                                         ObjectMapper objectMapper) {
        return new UnauthorizedEntryPoint(httpStatusMapper, errorCodeMapper, errorMessageMapper, objectMapper);
    }

    @Bean
    public ApiErrorResponseAccessDeniedHandler apiErrorResponseAccessDeniedHandler(ObjectMapper objectMapper,
                                                                                  HttpStatusMapper httpStatusMapper,
                                                                                  ErrorCodeMapper errorCodeMapper,
                                                                                  ErrorMessageMapper errorMessageMapper) {
        return new ApiErrorResponseAccessDeniedHandler(objectMapper, httpStatusMapper, errorCodeMapper,
                errorMessageMapper);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Roles.ADMIN).implies(Roles.OPERATOR)
                .role(Roles.OPERATOR).implies(Roles.REGISTERED)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthConverter jwtAuthConverter,
                                           UserProvisioningFilter userProvisioningFilter,
                                           AccessDeniedHandler accessDeniedHandler,
                                           UnauthorizedEntryPoint unauthorizedEntryPoint
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        // ANONYMOUS: read-only access to the public match data
                        .requestMatchers("/", "/ws/**").permitAll()
                        // Tomcat re-enters the chain for the ERROR dispatch; denying it here
                        // would replace the real error with an authorization failure.
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/clubs/**", "/api/players/**", "/matches/**").permitAll()

                        // OPERATOR: records what happens during a match
                        .requestMatchers(HttpMethod.POST, "/matches/*/events").hasRole(Roles.OPERATOR)

                        // ADMIN: owns the reference data and the match calendar
                        .requestMatchers("/api/clubs/**", "/api/players/**").hasRole(Roles.ADMIN)
                        .requestMatchers("/matches/**").hasRole(Roles.ADMIN)

                        // REGISTERED: anything else needs a valid token
                        .anyRequest().hasRole(Roles.REGISTERED))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Set on both: oauth2ResourceServer installs its own handler for requests
                // that carry a bearer token, which would otherwise win.
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .addFilterAfter(userProvisioningFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }

}
