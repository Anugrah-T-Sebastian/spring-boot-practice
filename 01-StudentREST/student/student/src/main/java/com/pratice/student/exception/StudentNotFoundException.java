package com.pratice.student.exception;

import org.springframework.http.HttpStatusCode;

public class StudentNotFoundException extends RuntimeException {
    public HttpStatusCode getStatus;

    public StudentNotFoundException(Long id) {
        super("Student not found with ID: " + id);
    }
}
