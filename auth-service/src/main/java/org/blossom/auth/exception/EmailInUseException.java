package org.blossom.auth.exception;

public class EmailInUseException extends Exception {
    public EmailInUseException() {
        super();
    }

    public EmailInUseException(String message) {
        super(message);
    }

    public EmailInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailInUseException(Throwable cause) {
        super(cause);
    }

    protected EmailInUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
