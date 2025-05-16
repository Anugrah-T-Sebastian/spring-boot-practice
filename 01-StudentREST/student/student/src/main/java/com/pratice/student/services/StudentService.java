package com.pratice.student.services;

import com.pratice.student.dto.AddCoursesRequestDto;
import com.pratice.student.dto.StudentRequestDto;
import com.pratice.student.entity.Course;
import com.pratice.student.entity.Student;
import com.pratice.student.exception.StudentNotFoundException;
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

    @Transactional(Transactional.TxType.REQUIRED)
    public Student createStudent(StudentRequestDto studentRequestDTO) {
        Student student = Student.builder()
                .name(studentRequestDTO.getName())
                .build();
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAllWithCourse();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Course> getCoursesForStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId))
                .getCourses();
//        return courseService.getCoursesForStudent(studentById);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Course> addCoursesForStudent(Long studentId, List<AddCoursesRequestDto> addCoursesRequestDtos) {
        Student student = studentRepository.findStudentById(studentId);
        List<Course> studentCourses = addCoursesRequestDtos
                .stream()
                .map(dto -> Course.builder()
                        .courseName(dto.getName())
                        .student(student)
                        .build()
                )
                .toList();

//      return courseService.createCoursesForStudent(studentCourses);

        student.getCourses().addAll(studentCourses);
        return studentRepository.save(student).getCourses();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void transferCoursesFromStudent(Long studentId1, Long studentId2) {
        Student studentFrom = studentRepository.findById(studentId1).get();
        Student studentTo = studentRepository.findById(studentId2).get();
        courseService.getCoursesForStudent(studentFrom)
                .stream()
                .forEach(course -> course.setStudent(studentTo));
    }
}