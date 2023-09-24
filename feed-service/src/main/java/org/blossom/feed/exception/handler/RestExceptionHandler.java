package org.blossom.feed.exception.handler;

import org.blossom.feed.exception.UserNotFoundException;
import org.blossom.feed.exception.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> userNotFoundException(UserNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorMessage message = new ErrorMessage(status, UserNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }
}