package com.usersaddresses.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var errors = exception.getBindingResult().getFieldErrors().stream().map(fieldError ->
                ErrorResponse.builder()
                        .errorMessage(String.format("Filed %s cannot be null or empty", fieldError.getField()))
                        .build())
                .collect(Collectors.toList());
        log.info("Errors: {}",  errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder().errorMessage("Request contains invalid data").build());
    }

    @ExceptionHandler(UserNotExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUserNotExistsException(UserNotExistsException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder().errorMessage(exception.getMessage()).build());
    }
}

