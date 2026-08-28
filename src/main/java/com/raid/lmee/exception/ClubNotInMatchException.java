package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * The command names a club that is neither the home nor the away side.
 * <p>
 * Unlike the {@link MatchRuleViolationException} family this can never succeed later, so it is a bad
 * request and not a conflict.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ResponseErrorCode("CLUB_NOT_IN_MATCH")
public class ClubNotInMatchException extends LmeeException {

    @ResponseErrorProperty
    private final UUID matchId;

    @ResponseErrorProperty
    private final UUID clubId;

    public ClubNotInMatchException(final UUID matchId, final UUID clubId) {
        super("club " + clubId + " does not take part in match " + matchId);
        this.matchId = matchId;
        this.clubId = clubId;
    }

    public UUID getMatchId() {
        return matchId;
    }

    public UUID getClubId() {
        return clubId;
    }

}
