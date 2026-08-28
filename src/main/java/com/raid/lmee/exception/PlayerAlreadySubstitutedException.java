package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * The player was substituted off and cannot return.
 */
@ResponseErrorCode("PLAYER_ALREADY_SUBSTITUTED")
public class PlayerAlreadySubstitutedException extends MatchRuleViolationException {

    @ResponseErrorProperty
    private final UUID playerId;

    public PlayerAlreadySubstitutedException(final UUID matchId, final UUID playerId) {
        super("player " + playerId + " has already been substituted off in match " + matchId, matchId);
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

}
