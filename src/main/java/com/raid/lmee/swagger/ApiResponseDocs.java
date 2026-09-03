package com.raid.lmee.swagger;

public final class ApiResponseDocs {

    public static final String BEARER_SCHEME = "bearerAuth";

    public static final String OK = "Request succeeded.";
    public static final String CREATED = "Resource created. The body carries its identifier.";
    public static final String ACCEPTED = "Command accepted and applied to the match.";
    public static final String NO_CONTENT = "Resource deleted.";

    public static final String BAD_REQUEST =
            "The payload fails validation (code VALIDATION_FAILED), carries a value that cannot be "
                    + "converted to its declared type (ARGUMENT_TYPE_MISMATCH), or references data "
                    + "that does not belong together (CLUB_NOT_IN_MATCH, PLAYER_NOT_IN_CLUB).";
    public static final String MALFORMED_IDENTIFIER =
            "An identifier in the path is not a valid UUID.";
    public static final String UNAUTHORIZED =
            "No access token was sent, or the token is expired or invalid. Written by the security "
                    + "entry point, which emits only a code and a message.";
    public static final String FORBIDDEN =
            "The token is valid but the caller lacks the required role. Written by the security "
                    + "access denied handler, which emits only a code and a message.";
    public static final String NOT_FOUND =
            "No resource exists for the given identifier. The code names the resource: "
                    + "CLUB_NOT_FOUND, PLAYER_NOT_FOUND or MATCH_NOT_FOUND.";
    public static final String CONFLICT =
            "The request is valid on its own but breaks a match rule in the current match state. The "
                    + "code names the rule, for example INVALID_MATCH_STATE, PENALTY_ALREADY_PENDING, "
                    + "NO_GOAL_TO_CANCEL, SUBSTITUTION_LIMIT_REACHED or PLAYER_SENT_OFF.";
    public static final String INTERNAL_ERROR =
            "Unexpected failure, or stored match data that is inconsistent "
                    + "(MATCH_STREAM_CORRUPTED, MATCH_PROJECTION_OUT_OF_SYNC). The incident is "
                    + "logged with the timestamp returned in the body.";

    public static final String EXAMPLE_VALIDATION_FAILED = """
            {
              "status": 400,
              "code": "VALIDATION_FAILED",
              "message": "Validation failed for object='clubDTO'. Error count: 1",
              "timestamp": "2026-09-03T14:35:12.482+02:00",
              "fieldErrors": [
                {
                  "code": "REQUIRED_NOT_BLANK",
                  "property": "name",
                  "message": "must not be blank",
                  "rejectedValue": "",
                  "path": "name"
                }
              ]
            }""";

    public static final String EXAMPLE_TYPE_MISMATCH = """
            {
              "status": 400,
              "code": "ARGUMENT_TYPE_MISMATCH",
              "message": "Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'",
              "timestamp": "2026-09-03T14:35:12.482+02:00",
              "expectedType": "java.util.UUID",
              "property": "matchId",
              "rejectedValue": "not-a-uuid"
            }""";

    public static final String EXAMPLE_INCONSISTENT_REFERENCE = """
            {
              "status": 400,
              "code": "PLAYER_NOT_IN_CLUB",
              "message": "Player 8c7fb13c-0924-47d4-821a-36f73558c898 does not play for club 1d4e2f60-9b3a-4c81-90ad-27c5f0b6e412",
              "timestamp": "2026-09-03T14:35:12.482+02:00"
            }""";

    public static final String EXAMPLE_UNAUTHORIZED = """
            {
              "code": "INSUFFICIENT_AUTHENTICATION",
              "message": "Full authentication is required to access this resource"
            }""";

    public static final String EXAMPLE_FORBIDDEN = """
            {
              "code": "ACCESS_DENIED",
              "message": "Access is denied"
            }""";

    public static final String EXAMPLE_NOT_FOUND = """
            {
              "status": 404,
              "code": "MATCH_NOT_FOUND",
              "message": "Match 3f2b1c9e-0a4d-4a7e-8b12-8d5f6c7e9a01 does not exist",
              "timestamp": "2026-09-03T14:35:12.482+02:00"
            }""";

    public static final String EXAMPLE_CONFLICT = """
            {
              "status": 409,
              "code": "PENALTY_ALREADY_PENDING",
              "message": "A penalty is already pending for this match",
              "timestamp": "2026-09-03T14:35:12.482+02:00"
            }""";

    public static final String EXAMPLE_INTERNAL_ERROR = """
            {
              "status": 500,
              "code": "MATCH_STREAM_CORRUPTED",
              "message": "Stored match data is inconsistent. The incident has been logged.",
              "timestamp": "2026-09-03T14:35:12.482+02:00"
            }""";

    private ApiResponseDocs() {
    }

}
