package org.blossom.message.exception;

public class IllegalMessageOperationException extends Exception {
    public IllegalMessageOperationException() {
        super();
    }

    public IllegalMessageOperationException(String message) {
        super(message);
    }

    public IllegalMessageOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalMessageOperationException(Throwable cause) {
        super(cause);
    }

    protected IllegalMessageOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
