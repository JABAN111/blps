package org.example.blps_lab1.common.exceptions;

public class StatusAlreadySetException extends RuntimeException{
    public StatusAlreadySetException(String message) {
        super(message);
    }
}
