package org.blossom.message.exception;

public class IllegalChatOperationException extends Exception {
    public IllegalChatOperationException() {
        super();
    }

    public IllegalChatOperationException(String message) {
        super(message);
    }

    public IllegalChatOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalChatOperationException(Throwable cause) {
        super(cause);
    }

    protected IllegalChatOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
