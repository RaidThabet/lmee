package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

@ResponseErrorCode("PLAYER_NOT_FOUND")
public class PlayerNotFoundException extends ResourceNotFoundException {

    public PlayerNotFoundException(final UUID playerId) {
        super("player " + playerId + " not found", playerId);
    }

}
