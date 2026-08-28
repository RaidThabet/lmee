package com.raid.lmee.exception;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * A resource addressed by id does not exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class ResourceNotFoundException extends LmeeException {

    @ResponseErrorProperty("id")
    private final UUID resourceId;

    protected ResourceNotFoundException(final String message, final UUID resourceId) {
        super(message);
        this.resourceId = resourceId;
    }

    public UUID getResourceId() {
        return resourceId;
    }

}
