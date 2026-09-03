package com.raid.lmee.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = ApiResponseDocs.BEARER_SCHEME)
@ApiResponse(responseCode = "204", description = ApiResponseDocs.NO_CONTENT, content = @Content)
@ApiResponse(responseCode = "400", description = ApiResponseDocs.MALFORMED_IDENTIFIER,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiTypeMismatchErrorSchema.class),
                examples = @ExampleObject(value = ApiResponseDocs.EXAMPLE_TYPE_MISMATCH)))
@ApiResponse(responseCode = "401", description = ApiResponseDocs.UNAUTHORIZED,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorSchema.class),
                examples = @ExampleObject(value = ApiResponseDocs.EXAMPLE_UNAUTHORIZED)))
@ApiResponse(responseCode = "403", description = ApiResponseDocs.FORBIDDEN,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorSchema.class),
                examples = @ExampleObject(value = ApiResponseDocs.EXAMPLE_FORBIDDEN)))
@ApiResponse(responseCode = "404", description = ApiResponseDocs.NOT_FOUND,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorSchema.class),
                examples = @ExampleObject(value = ApiResponseDocs.EXAMPLE_NOT_FOUND)))
@ApiResponse(responseCode = "500", description = ApiResponseDocs.INTERNAL_ERROR,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorSchema.class),
                examples = @ExampleObject(value = ApiResponseDocs.EXAMPLE_INTERNAL_ERROR)))
public @interface ApiDeleteResponses {
}
