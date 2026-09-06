package com.raid.lmee.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateMatchRequest(
        @NotNull(message = "Kickoff must be set") OffsetDateTime scheduledKickoff,
        @NotBlank(message = "Venue cannot be blank") String venue,
        @NotNull(message = "Home team ID is required") UUID homeTeamId,
        @NotNull(message = "Away team ID is required") UUID awayTeamId
) {
}
