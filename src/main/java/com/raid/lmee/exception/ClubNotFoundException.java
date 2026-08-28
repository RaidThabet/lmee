package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

@ResponseErrorCode("CLUB_NOT_FOUND")
public class ClubNotFoundException extends ResourceNotFoundException {

    public ClubNotFoundException(final UUID clubId) {
        super("club " + clubId + " not found", clubId);
    }

}
