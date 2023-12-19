package org.blossom.auth.exception.handler;

import org.blossom.auth.exception.*;
import org.blossom.auth.exception.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EmailInUseException.class)
    public ResponseEntity<ErrorMessage> emailInUseException(EmailInUseException exception, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorMessage message = new ErrorMessage(status, EmailInUseException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(EmailNotInUseException.class)
    public ResponseEntity<ErrorMessage> emailNotInUseException(EmailNotInUseException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, EmailNotInUseException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(UsernameInUseException.class)
    public ResponseEntity<ErrorMessage> usernameInUseException(UsernameInUseException exception, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorMessage message = new ErrorMessage(status, UsernameInUseException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(LoginCredentialsException.class)
    public ResponseEntity<ErrorMessage> loginCredentialsException(LoginCredentialsException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorMessage message = new ErrorMessage(status, LoginCredentialsException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(NoRoleFoundException.class)
    public ResponseEntity<ErrorMessage> noRoleFoundException(NoRoleFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, NoRoleFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> userNotFoundException(UserNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage message = new ErrorMessage(status, UserNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorMessage> invalidTokenException(InvalidTokenException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorMessage message = new ErrorMessage(status, InvalidTokenException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorMessage> tokenNotFoundException(TokenNotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorMessage message = new ErrorMessage(status, TokenNotFoundException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> badCredentialsException(BadCredentialsException exception, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorMessage message = new ErrorMessage(status, BadCredentialsException.class.getName(), exception.getMessage(), new Date());
        return ResponseEntity.status(status).body(message);
    }
}