package org.blossom.message.exception;

public class InvalidChatException extends Exception {
    public InvalidChatException() {
        super();
    }

    public InvalidChatException(String message) {
        super(message);
    }

    public InvalidChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidChatException(Throwable cause) {
        super(cause);
    }

    protected InvalidChatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
