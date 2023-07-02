package org.blossom.auth.exception;

public class EmailNotInUseException extends Exception {
    public EmailNotInUseException() {
        super();
    }

    public EmailNotInUseException(String message) {
        super(message);
    }

    public EmailNotInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotInUseException(Throwable cause) {
        super(cause);
    }

    protected EmailNotInUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}