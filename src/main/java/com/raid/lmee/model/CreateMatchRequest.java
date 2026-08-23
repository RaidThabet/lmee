package com.raid.lmee.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateMatchRequest(
        OffsetDateTime scheduledKickoff,
        String venue,
        UUID homeTeamId,
        UUID awayTeamId
) {
}
