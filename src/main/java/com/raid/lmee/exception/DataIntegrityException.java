package com.raid.lmee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Stored data contradicts an invariant the application relies on. The caller did nothing wrong, so
 * this is a server error and is logged with a stack trace.
 * <p>
 * {@code error.handling.messages} replaces the message of these exceptions in the response body:
 * the detail names internal ids and is only useful in the log.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class DataIntegrityException extends LmeeException {

    protected DataIntegrityException(final String message) {
        super(message);
    }

}
