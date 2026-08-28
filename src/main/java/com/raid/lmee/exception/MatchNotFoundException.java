package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

@ResponseErrorCode("MATCH_NOT_FOUND")
public class MatchNotFoundException extends ResourceNotFoundException {

    public MatchNotFoundException(final UUID matchId) {
        super("match " + matchId + " not found", matchId);
    }

}
