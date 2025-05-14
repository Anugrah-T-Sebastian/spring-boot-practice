package com.pratice.student.controllers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@NoArgsConstructor
@Controller
@RestController("/api/v1/student")
public class StudentController {

    @GetMapping("/")
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok("All students");
    }

    @PostMapping("/")
    public ResponseEntity<?> createStudent(@RequestBody String student) {
        return ResponseEntity.ok("Created student");
    }

    @GetMapping("/{studentId}/courses")
    public ResponseEntity<?> getCoursesById(@PathVariable int studentId) {
        return ResponseEntity.ok("Student ID");
    }

    @PostMapping("/{studentId}/course/{courseId}")
    public ResponseEntity<?> addStudentToCourse(@PathVariable int studentId, @PathVariable int courseId) {
        return  ResponseEntity.ok("Student added to course");
    }
}
