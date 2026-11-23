package com.rideshare.rideservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobleExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Exception occurred: ", e);
        return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }

    // RuntimeException handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException occurred: ", e);
        return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
}
