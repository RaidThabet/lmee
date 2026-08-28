package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * A penalty outcome was reported for a club that has no penalty awarded to it.
 */
@ResponseErrorCode("NO_PENDING_PENALTY")
public class NoPendingPenaltyException extends MatchRuleViolationException {

    @ResponseErrorProperty
    private final UUID clubId;

    public NoPendingPenaltyException(final UUID matchId, final UUID clubId) {
        super("no penalty is awarded to club " + clubId + " in match " + matchId, matchId);
        this.clubId = clubId;
    }

    public UUID getClubId() {
        return clubId;
    }

}
