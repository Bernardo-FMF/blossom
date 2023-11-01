package org.blossom.activity.exception.handler;

import org.blossom.activity.exception.*;
import org.blossom.activity.exception.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorMessage> commentNotFoundException(CommentNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorMessage message = new ErrorMessage(status, CommentNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<ErrorMessage> operationNotAllowedException(OperationNotAllowedException exception, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorMessage message = new ErrorMessage(status, OperationNotAllowedException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorMessage> postNotFoundException(PostNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, PostNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> userNotFoundException(UserNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, UserNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(InteractionAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> interactionAlreadyExistsException(InteractionAlreadyExistsException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, InteractionAlreadyExistsException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(InteractionNotFoundException.class)
    public ResponseEntity<ErrorMessage> interactionNotFoundException(InteractionNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, InteractionNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }
}