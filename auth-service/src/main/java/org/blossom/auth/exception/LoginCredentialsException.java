package org.blossom.auth.exception;

public class LoginCredentialsException extends Exception {
    public LoginCredentialsException() {
        super();
    }

    public LoginCredentialsException(String message) {
        super(message);
    }

    public LoginCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginCredentialsException(Throwable cause) {
        super(cause);
    }

    protected LoginCredentialsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}