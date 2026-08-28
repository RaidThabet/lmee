package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

/**
 * The read model is missing a row the incoming event needs, so the projection ran against a
 * different history than the event store holds.
 */
@ResponseErrorCode("MATCH_PROJECTION_OUT_OF_SYNC")
public class MatchProjectionOutOfSyncException extends DataIntegrityException {

    public MatchProjectionOutOfSyncException(final UUID matchId, final String detail) {
        super("projection of match " + matchId + " is out of sync: " + detail);
    }

}
