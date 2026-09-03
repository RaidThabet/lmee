package com.raid.lmee.config;

import com.raid.lmee.swagger.*;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Document-level OpenAPI metadata.
 * <p>
 * The bearer scheme is declared here but not applied globally: the read endpoints are open to
 * anonymous callers, so the requirement is attached per operation by the composed annotations that
 * document a write ({@link ApiCreateResponses}, {@link ApiUpdateResponses},
 * {@link ApiDeleteResponses}, {@link ApiCommandResponses}).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lmeeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("LMEE API")
                        .version("v1")
                        .description("""
                                Live match event engine. Reference data (clubs, players) is managed by
                                an administrator, match events are recorded by an operator, and every
                                read endpoint is open to anonymous callers.

                                Send an access token issued by Keycloak as `Authorization: Bearer <token>`
                                to call a write endpoint.
                                """))
                .components(new Components()
                        .addSecuritySchemes(ApiResponseDocs.BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access token issued by the Keycloak realm of this environment.")));
    }

}
