package org.example.blps_lab1.config.security.exception;

public class JwtTokenExpiredException extends RuntimeException {
    public JwtTokenExpiredException(final String message) {
        super(message);
    }
}
