package org.blossom.activity.exception;

public class InteractionNotFoundException extends Exception {
    public InteractionNotFoundException() {
        super();
    }

    public InteractionNotFoundException(String message) {
        super(message);
    }

    public InteractionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InteractionNotFoundException(Throwable cause) {
        super(cause);
    }

    protected InteractionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}