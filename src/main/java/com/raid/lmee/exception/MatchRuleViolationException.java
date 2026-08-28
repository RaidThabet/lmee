package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * A command was well formed but the match it targets does not allow it right now.
 * <p>
 * These are conflicts rather than bad requests: the same payload can succeed once the match reaches
 * another state.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public abstract class MatchRuleViolationException extends LmeeException {

    @ResponseErrorProperty
    private final UUID matchId;

    protected MatchRuleViolationException(final String message, final UUID matchId) {
        super(message);
        this.matchId = matchId;
    }

    public UUID getMatchId() {
        return matchId;
    }

}
