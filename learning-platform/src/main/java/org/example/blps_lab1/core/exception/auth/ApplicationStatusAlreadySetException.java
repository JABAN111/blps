package org.example.blps_lab1.core.exception.auth;

public class ApplicationStatusAlreadySetException extends RuntimeException{
    public ApplicationStatusAlreadySetException(String message) {
        super(message);
    }
}
