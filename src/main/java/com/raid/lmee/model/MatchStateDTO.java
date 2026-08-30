package com.raid.lmee.model;

import java.util.UUID;

/**
 * Read model of a match: the current scoreline and tallies as the projection holds them.
 * <p>
 * The scores stay nullable because a match that has not kicked off has no scoreline yet, while the
 * card and substitution tallies are zero from the moment the projection row exists.
 */
public record MatchStateDTO(
        UUID matchId,
        MatchStatus status,
        String homeClubName,
        String awayClubName,
        Integer homeScore,
        Integer awayScore,
        int homeYellows,
        int awayYellows,
        int homeReds,
        int awayReds,
        int homeSubs,
        int awaySubs
) {
}
