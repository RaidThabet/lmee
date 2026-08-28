package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

/**
 * Replaying the event stream produced a state the decide methods can never reach, so the stream
 * itself is wrong.
 */
@ResponseErrorCode("MATCH_STREAM_CORRUPTED")
public class MatchStreamCorruptedException extends DataIntegrityException {

    public MatchStreamCorruptedException(final UUID matchId, final String detail) {
        super("event stream of match " + matchId + " is corrupt: " + detail);
    }

}
