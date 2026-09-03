package com.raid.lmee.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ApiValidationError",
        description = "Body returned when the request payload fails validation. The code is always "
                + "VALIDATION_FAILED.")
public class ApiValidationErrorSchema extends ApiErrorSchema {

    @Schema(description = "One entry per rejected property of the request payload.")
    public List<FieldError> fieldErrors;

    @Schema(name = "ApiFieldError", description = "A rejected property of the request payload.")
    public static class FieldError {

        @Schema(description = "Identifier of the violated constraint.", example = "REQUIRED_NOT_BLANK")
        public String code;

        @Schema(description = "Name of the rejected property.", example = "name")
        public String property;

        @Schema(description = "Explanation of the violated constraint.", example = "must not be blank")
        public String message;

        @Schema(description = "Value that was rejected.", example = "")
        public Object rejectedValue;

        @Schema(description = "Full path of the property inside the payload. Differs from "
                + "\"property\" only for a nested object.", example = "name")
        public String path;

    }

}
