package org.blossom.feed.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ErrorMessage {
    private HttpStatus status;
    private String title;
    private String message;
    private Date timestamp;
}