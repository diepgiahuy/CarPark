package org.example.carpark.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for handling various exceptions across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles CarParkAvailabilityException and RuntimeException.
     *
     * @param e the exception
     * @return a ResponseEntity with the exception message and HTTP status 500
     */
    @ExceptionHandler({CarParkAvailabilityException.class, RuntimeException.class})
    public ResponseEntity<String> handleCarParkAvailabilityException(CarParkAvailabilityException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles generic exceptions.
     *
     * @param e the exception
     * @return a ResponseEntity with a generic error message and HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles MissingServletRequestParameterException.
     *
     * @param ex the exception
     * @return a ResponseEntity with the missing parameter details and HTTP status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        String paramName = ex.getParameterName();
        error.put(paramName, ex.getBody().getDetail());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param ex the exception
     * @return a ResponseEntity with the constraint violation details and HTTP status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}