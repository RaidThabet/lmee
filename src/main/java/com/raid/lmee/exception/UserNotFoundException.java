package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;

import java.util.UUID;

@ResponseErrorCode("USER_NOT_FOUND")
public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(UUID userId) {
        super("User with keycloak id " + userId + " is not found.", userId);
    }
}
