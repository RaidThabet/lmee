package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * A penalty was awarded while an earlier one still waits for its outcome.
 */
@ResponseErrorCode("PENALTY_ALREADY_PENDING")
public class PenaltyAlreadyPendingException extends MatchRuleViolationException {

    @ResponseErrorProperty
    private final UUID pendingClubId;

    public PenaltyAlreadyPendingException(final UUID matchId, final UUID pendingClubId) {
        super("a penalty for club " + pendingClubId + " is already awaiting its outcome", matchId);
        this.pendingClubId = pendingClubId;
    }

    public UUID getPendingClubId() {
        return pendingClubId;
    }

}
