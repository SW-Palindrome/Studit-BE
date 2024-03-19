package com.palindrome.studit.global.error;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handlerEntityNotFoundException(EntityNotFoundException e) {
        log.error("handleEntityNotFoundException = {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("handleEntityNotFoundException", e.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handlerIllegalArgumentException(IllegalArgumentException e) {
        log.error("handlerIllegalArgumentException = {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("handlerIllegalArgumentException", e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
