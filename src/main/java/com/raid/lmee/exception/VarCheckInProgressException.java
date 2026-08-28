package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

/**
 * A VAR check is still awaiting its decision, so the match cannot move on yet.
 */
@ResponseErrorCode("VAR_CHECK_IN_PROGRESS")
public class VarCheckInProgressException extends MatchRuleViolationException {

    public VarCheckInProgressException(final UUID matchId) {
        super("a var check in match " + matchId + " is awaiting a decision", matchId);
    }

}
