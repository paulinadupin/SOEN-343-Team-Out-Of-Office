package com.enchantea.ui;

import com.enchantea.application.ord.OrderNotFoundException;
import com.enchantea.application.ord.contracts.CommandRejectedException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Ensures rejection messages (e.g. "This promo code has expired") reach the client
// instead of a generic "Bad Request".
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommandRejectedException.class)
    public ResponseEntity<Map<String, String>> handleCommandRejected(CommandRejectedException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotFound(OrderNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBusinessRuleViolation(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", exception.getMessage()));
    }
}
