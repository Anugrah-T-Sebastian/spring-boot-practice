package com.pratice.student.repositories;

import com.pratice.student.entity.Course;
import com.pratice.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByStudent(Student studentById);
}
