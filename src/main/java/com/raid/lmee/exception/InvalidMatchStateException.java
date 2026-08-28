package com.raid.lmee.exception;

import com.raid.lmee.model.MatchStatus;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * The match is not in the status (and half) the requested command needs.
 * <p>
 * {@code expected} is a human readable description of the precondition, so one code covers every
 * lifecycle guard while the payload still tells the caller what was required.
 */
@ResponseErrorCode("INVALID_MATCH_STATE")
public class InvalidMatchStateException extends MatchRuleViolationException {

    @ResponseErrorProperty(includeIfNull = true)
    private final MatchStatus currentStatus;

    @ResponseErrorProperty(includeIfNull = true)
    private final Integer currentHalf;

    @ResponseErrorProperty
    private final String expected;

    public InvalidMatchStateException(
            final UUID matchId,
            final MatchStatus currentStatus,
            final Integer currentHalf,
            final String expected
    ) {
        super("match " + matchId + " is " + currentStatus + " (half " + currentHalf + ") but " + expected, matchId);
        this.currentStatus = currentStatus;
        this.currentHalf = currentHalf;
        this.expected = expected;
    }

    public MatchStatus getCurrentStatus() {
        return currentStatus;
    }

    public Integer getCurrentHalf() {
        return currentHalf;
    }

    public String getExpected() {
        return expected;
    }

}
