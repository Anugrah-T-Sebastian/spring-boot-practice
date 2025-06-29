package com.jobs.jobManagement.exception;

import org.springframework.http.HttpStatus;

public class EmployeeNotFound extends RuntimeException {
    public EmployeeNotFound(String message) {
        super(message);
    }
}
