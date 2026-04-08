package com.hostelcare.exception;

/**
 * Thrown when a user attempts an operation they are not permitted to perform.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
