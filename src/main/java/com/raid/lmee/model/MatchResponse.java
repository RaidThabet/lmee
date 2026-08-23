package com.raid.lmee.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MatchResponse(
        UUID matchId,
        OffsetDateTime scheduledKickoff,
        MatchStatus status,
        String homeClubName,
        String awayClubName
) {
}
