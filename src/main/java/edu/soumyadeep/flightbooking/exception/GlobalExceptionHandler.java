package edu.soumyadeep.flightbooking.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Handle @Valid validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");

        List<Map<String, String>> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            Map<String, String> fieldError = new HashMap<>();
            fieldError.put("field", err.getField());
            fieldError.put("message", err.getDefaultMessage());
            details.add(fieldError);
        });

        response.put("details", details);
        return ResponseEntity.badRequest().body(response);
    }

    // 🔹 Handle duplicate email (unique constraint violation)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Duplicate entry detected. Possibly the email already exists.";
        if (ex.getMessage() != null && ex.getMessage().contains("users(EMAIL")) {
            message = "Email already exists. Please use a different one.";
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Data integrity violation");
        response.put("message", message);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 🔹 Handle user not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "User Not Found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 🔹 Handle user with no bookings
    @ExceptionHandler(UserHasNoBookingsException.class)
    public ResponseEntity<Map<String, Object>> handleUserHasNoBookings(UserHasNoBookingsException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 🔹 Handle other runtime exceptions safely
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        // Special case: missing return date
        if (ex.getMessage() != null && ex.getMessage().contains("Return date is required")) {
            response.put("error", "Bad Request");
            response.put("message", "Missing return date for round-trip booking");
        } else {
            response.put("error", "Unexpected error occurred");
            response.put("message", ex.getMessage() != null ? ex.getMessage() : "Unknown error");
        }

        return ResponseEntity.badRequest().body(response);
    }
}