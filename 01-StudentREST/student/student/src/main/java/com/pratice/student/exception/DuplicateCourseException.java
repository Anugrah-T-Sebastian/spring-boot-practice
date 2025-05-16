package com.pratice.student.exception;

public class DuplicateCourseException extends RuntimeException {
    public DuplicateCourseException(String courseName) {
        super("Course " + courseName + " already exists for this student");
    }
}
