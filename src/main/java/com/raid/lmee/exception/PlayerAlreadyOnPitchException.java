package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * The player is already on the pitch and cannot be brought on.
 */
@ResponseErrorCode("PLAYER_ALREADY_ON_PITCH")
public class PlayerAlreadyOnPitchException extends MatchRuleViolationException {

    @ResponseErrorProperty
    private final UUID playerId;

    public PlayerAlreadyOnPitchException(final UUID matchId, final UUID playerId) {
        super("player " + playerId + " is already on the pitch in match " + matchId, matchId);
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

}
