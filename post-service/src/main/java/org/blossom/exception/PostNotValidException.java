package org.blossom.exception;

public class PostNotValidException extends Exception {
    public PostNotValidException() {
        super();
    }

    public PostNotValidException(String message) {
        super(message);
    }

    public PostNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostNotValidException(Throwable cause) {
        super(cause);
    }

    protected PostNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}