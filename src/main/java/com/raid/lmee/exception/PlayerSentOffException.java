package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;

import java.util.UUID;

/**
 * The player has been shown a red card and takes no further part in the match.
 */
@ResponseErrorCode("PLAYER_SENT_OFF")
public class PlayerSentOffException extends MatchRuleViolationException {

    @ResponseErrorProperty
    private final UUID playerId;

    public PlayerSentOffException(final UUID matchId, final UUID playerId) {
        super("player " + playerId + " has been sent off in match " + matchId, matchId);
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

}
