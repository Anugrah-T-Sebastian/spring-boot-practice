package com.pratice.student.services;

import com.pratice.student.dto.AddCoursesRequestDto;
import com.pratice.student.dto.StudentRequestDto;
import com.pratice.student.entity.Course;
import com.pratice.student.entity.Student;
import com.pratice.student.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final CourseService courseService;
    private final StudentRepository studentRepository;

    public Student createStudent(StudentRequestDto studentRequestDTO) {
        Student student = Student.builder()
                .name(studentRequestDTO.getName())
                .build();
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Course> getCoursesForStudent(Long studentId) {
        Student studentById = studentRepository.findStudentById(studentId);
       return courseService.getCourseForStudent(studentById);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<Course> addCoursesForStudent(Long studentId, List<AddCoursesRequestDto> addCoursesRequestDtos) {
        Student studentById = studentRepository.findStudentById(studentId);
        List<Course> studentCourses = addCoursesRequestDtos
                .stream()
                .map(dto -> Course.builder()
                        .courseName(dto.getName())
                        .student(studentById)
                        .build()
                )
                .collect(Collectors.toList());
        return courseService.createCoursesForStudent(studentCourses);
    }
}