package com.pratice.student.controllers;

import com.pratice.student.dto.AddCoursesRequestDto;
import com.pratice.student.dto.StudentRequestDto;
import com.pratice.student.entity.Course;
import com.pratice.student.entity.Student;
import com.pratice.student.services.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/")
    public ResponseEntity<?> getAllStudents() {
        List<Student> allStudents = studentService.getAllStudents();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allStudents);
    }

    @PostMapping("/")
    public ResponseEntity<?> createStudent(@RequestBody StudentRequestDto studentRequestDTO) {
        Student student = studentService.createStudent(studentRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(student);
    }

    @GetMapping("/{studentId}/courses")
    public ResponseEntity<?> getCoursesById(@PathVariable Long studentId) {
        List<Course> coursesForStudent = studentService.getCoursesForStudent(studentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(coursesForStudent);
    }

    @PostMapping("/{studentId}/courses")
    public ResponseEntity<?> addStudentToCourse(@PathVariable Long studentId, @RequestBody List<AddCoursesRequestDto> addCoursesRequestDtos) {
        List<Course> courses = studentService.addCoursesForStudent(studentId, addCoursesRequestDtos);
        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courses);
    }
}
