package org.blossom.exception;

public class FollowNotValidException extends Exception {
    public FollowNotValidException() {
        super();
    }

    public FollowNotValidException(String message) {
        super(message);
    }

    public FollowNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public FollowNotValidException(Throwable cause) {
        super(cause);
    }

    protected FollowNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}