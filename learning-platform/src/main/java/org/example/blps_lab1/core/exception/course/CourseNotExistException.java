package org.example.blps_lab1.core.exception.course;

public class CourseNotExistException extends RuntimeException{
    public CourseNotExistException(String message) {
        super(message);
    }
}
