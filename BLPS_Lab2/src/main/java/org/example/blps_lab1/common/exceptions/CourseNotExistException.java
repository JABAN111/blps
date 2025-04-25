package org.example.blps_lab1.common.exceptions;

public class CourseNotExistException extends RuntimeException{
    public CourseNotExistException(String message) {
        super(message);
    }
}
