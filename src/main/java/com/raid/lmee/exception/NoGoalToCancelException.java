package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * A goal was asked to be cancelled while the club it would be taken from is on zero.
 */
@ResponseErrorCode("NO_GOAL_TO_CANCEL")
public class NoGoalToCancelException extends MatchRuleViolationException {

    @ResponseErrorProperty
    private final UUID clubId;

    public NoGoalToCancelException(final UUID matchId, final UUID clubId) {
        super("club " + clubId + " has no goal to cancel in match " + matchId, matchId);
        this.clubId = clubId;
    }

    public UUID getClubId() {
        return clubId;
    }

}
