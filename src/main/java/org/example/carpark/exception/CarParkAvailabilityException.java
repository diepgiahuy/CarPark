package org.example.carpark.exception;

public class CarParkAvailabilityException extends RuntimeException {
    public CarParkAvailabilityException(String message) {
        super(message);
    }

    public CarParkAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }
}