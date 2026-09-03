package com.raid.lmee.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiTypeMismatchError",
        description = "Body returned when a request value cannot be converted to the expected type. "
                + "The code is always ARGUMENT_TYPE_MISMATCH.")
public class ApiTypeMismatchErrorSchema extends ApiErrorSchema {

    @Schema(description = "Type the handler expects for the value.", example = "java.util.UUID")
    public String expectedType;

    @Schema(description = "Name of the parameter that carried the value.", example = "matchId")
    public String property;

    @Schema(description = "Value that could not be converted.", example = "not-a-uuid")
    public Object rejectedValue;

}
