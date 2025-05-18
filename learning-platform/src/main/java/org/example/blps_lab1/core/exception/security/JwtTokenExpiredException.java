package org.example.blps_lab1.core.exception.security;

public class JwtTokenExpiredException extends RuntimeException {
    public JwtTokenExpiredException(final String message) {
        super(message);
    }
}
