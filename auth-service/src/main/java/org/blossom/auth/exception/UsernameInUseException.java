package org.blossom.auth.exception;

public class UsernameInUseException extends Exception {
    public UsernameInUseException() {
        super();
    }

    public UsernameInUseException(String message) {
        super(message);
    }

    public UsernameInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameInUseException(Throwable cause) {
        super(cause);
    }

    protected UsernameInUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}