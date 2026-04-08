package com.hostelcare.exception;

/**
 * Thrown when a duplicate resource already exists (e.g., duplicate email/category).
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
