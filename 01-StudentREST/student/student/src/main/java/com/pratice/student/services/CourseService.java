package com.pratice.student.services;

import com.pratice.student.entity.Course;
import com.pratice.student.entity.Student;
import com.pratice.student.repositories.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class CourseService {
    private final CourseRepository courseRepository;

    public List<Course> getCoursesForStudent(Student student) {
        return courseRepository.findByStudent(student);
    }

    @Transactional
    public List<Course> createCoursesForStudent(List<Course> studentCourses) {
        return courseRepository.saveAll(studentCourses);
    }

    @Transactional
    public void deleteCoursesForStudent(Student student) {
        courseRepository.deleteByStudent(student);
    }


}
