package org.blossom.activity.exception;

public class InteractionAlreadyExistsException extends Exception {
    public InteractionAlreadyExistsException() {
        super();
    }

    public InteractionAlreadyExistsException(String message) {
        super(message);
    }

    public InteractionAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InteractionAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected InteractionAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}