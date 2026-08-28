package com.raid.lmee.exception;

/**
 * Root of every exception this application raises deliberately.
 * <p>
 * The error handling starter resolves the HTTP status and the error code by walking up the class
 * hierarchy (enabled through {@code error.handling.search-super-class-hierarchy}), so a family of
 * exceptions that shares a status declares {@code @ResponseStatus} once on its abstract parent.
 * Error codes stay on the concrete classes: an annotation on an abstract parent would be inherited
 * by every subclass and collapse them into a single code.
 */
public abstract class LmeeException extends RuntimeException {

    protected LmeeException(final String message) {
        super(message);
    }

    protected LmeeException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
