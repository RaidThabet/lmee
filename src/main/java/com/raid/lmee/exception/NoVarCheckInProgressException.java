package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

/**
 * A VAR decision was reported while no check is running.
 */
@ResponseErrorCode("NO_VAR_CHECK_IN_PROGRESS")
public class NoVarCheckInProgressException extends MatchRuleViolationException {

    public NoVarCheckInProgressException(final UUID matchId) {
        super("no var check in match " + matchId + " is awaiting a decision", matchId);
    }

}
