package com.hostelcare.exception;

/**
 * Thrown when a requested resource (entity) cannot be found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
