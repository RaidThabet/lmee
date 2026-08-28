package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * The command names a real player, but one who is on the books of another club.
 * <p>
 * Like {@link ClubNotInMatchException} this can never succeed later, so it is a bad request and not a
 * conflict.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ResponseErrorCode("PLAYER_NOT_IN_CLUB")
public class PlayerNotInClubException extends LmeeException {

    @ResponseErrorProperty
    private final UUID playerId;

    @ResponseErrorProperty
    private final UUID clubId;

    public PlayerNotInClubException(final UUID playerId, final UUID clubId) {
        super("player " + playerId + " does not play for club " + clubId);
        this.playerId = playerId;
        this.clubId = clubId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getClubId() {
        return clubId;
    }

}
