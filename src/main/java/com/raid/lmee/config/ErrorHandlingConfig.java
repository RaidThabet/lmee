package com.raid.lmee.config;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponseCustomizer;
import java.time.OffsetDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ErrorHandlingConfig {

    /**
     * Stamps every error response, so a report from a client can be matched against the log line the
     * starter wrote for the same exception.
     */
    @Bean
    public ApiErrorResponseCustomizer timestampApiErrorResponseCustomizer() {
        return response -> response.addErrorProperty("timestamp", OffsetDateTime.now());
    }

}
