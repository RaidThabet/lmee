package com.raid.lmee.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(responseCode = "200", description = ApiResponseDocs.OK, useReturnTypeSchema = true)
@ApiResponse(responseCode = "500", description = ApiResponseDocs.INTERNAL_ERROR,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiErrorSchema.class),
                examples = @ExampleObject(value = ApiResponseDocs.EXAMPLE_INTERNAL_ERROR)))
public @interface ApiGetListResponses {
}
