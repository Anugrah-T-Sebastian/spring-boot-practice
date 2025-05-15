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

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<Course> getCourseForStudent(Student studentById) {
        return courseRepository.findAllByStudent(studentById);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<Course> createCoursesForStudent(List<Course> studentCourses) {
        return courseRepository.saveAll(studentCourses);
    }
}
