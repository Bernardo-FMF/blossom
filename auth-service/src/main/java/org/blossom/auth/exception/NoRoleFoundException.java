package org.blossom.auth.exception;

public class NoRoleFoundException extends Exception {
    public NoRoleFoundException() {
        super();
    }

    public NoRoleFoundException(String message) {
        super(message);
    }

    public NoRoleFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRoleFoundException(Throwable cause) {
        super(cause);
    }

    protected NoRoleFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}