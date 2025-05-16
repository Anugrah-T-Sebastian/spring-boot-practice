package com.pratice.student.exception;

public class CourseValidationException extends RuntimeException {
    public CourseValidationException(String message) {
        super(message);
    }
}
