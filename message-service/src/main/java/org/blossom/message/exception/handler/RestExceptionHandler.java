package org.blossom.message.exception.handler;

import org.blossom.message.exception.*;
import org.blossom.message.exception.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ErrorMessage> chatNotFoundExceptionException(ChatNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, ChatNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(IllegalChatOperationException.class)
    public ResponseEntity<ErrorMessage> illegalChatOperationExceptionException(IllegalChatOperationException exception, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorMessage message = new ErrorMessage(status, IllegalChatOperationException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(IllegalMessageOperationException.class)
    public ResponseEntity<ErrorMessage> illegalMessageOperationExceptionException(IllegalMessageOperationException exception, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorMessage message = new ErrorMessage(status, IllegalMessageOperationException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(InvalidChatException.class)
    public ResponseEntity<ErrorMessage> invalidChatExceptionException(InvalidChatException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorMessage message = new ErrorMessage(status, InvalidChatException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorMessage> messageNotFoundExceptionException(MessageNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, MessageNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> userNotFoundExceptionException(UserNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, UserNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }
}