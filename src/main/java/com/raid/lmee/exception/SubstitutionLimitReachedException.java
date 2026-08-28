package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * The club has already used every substitution it is allowed.
 */
@ResponseErrorCode("SUBSTITUTION_LIMIT_REACHED")
public class SubstitutionLimitReachedException extends MatchRuleViolationException {

    @ResponseErrorProperty
    private final UUID clubId;

    @ResponseErrorProperty
    private final int limit;

    public SubstitutionLimitReachedException(final UUID matchId, final UUID clubId, final int limit) {
        super("club " + clubId + " already made its " + limit + " substitutions in match " + matchId, matchId);
        this.clubId = clubId;
        this.limit = limit;
    }

    public UUID getClubId() {
        return clubId;
    }

    public int getLimit() {
        return limit;
    }

}
