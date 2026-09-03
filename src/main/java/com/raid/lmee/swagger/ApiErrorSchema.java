package com.raid.lmee.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "ApiError", description = "Body returned for every failed request.")
public class ApiErrorSchema {

    @Schema(description = "HTTP status code, repeated in the body for clients that cannot read it "
            + "from the response line.", example = "404")
    public int status;

    @Schema(description = "Stable machine-readable identifier of the failure.", example = "MATCH_NOT_FOUND",
            requiredMode = Schema.RequiredMode.REQUIRED)
    public String code;

    @Schema(description = "Human-readable explanation, safe to show to a client.",
            example = "Match 3f2b1c9e-0a4d-4a7e-8b12-8d5f6c7e9a01 does not exist",
            requiredMode = Schema.RequiredMode.REQUIRED)
    public String message;

    @Schema(description = "Moment the error was produced, used to match a client report against the "
            + "server log line. Added by an ApiErrorResponseCustomizer, so it is present on every "
            + "error the exception handler produces.", example = "2026-09-03T14:35:12.482+02:00")
    public OffsetDateTime timestamp;

}
