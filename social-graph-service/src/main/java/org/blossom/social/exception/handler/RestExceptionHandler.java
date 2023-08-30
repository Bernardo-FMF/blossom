package org.blossom.social.exception.handler;


import org.blossom.social.exception.FollowNotValidException;
import org.blossom.social.exception.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(FollowNotValidException.class)
    public ResponseEntity<ErrorMessage> followNotValidException(FollowNotValidException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorMessage message = new ErrorMessage(status, FollowNotValidException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }
}