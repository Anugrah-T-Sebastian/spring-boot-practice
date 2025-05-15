package com.pratice.student.repositories;

import com.pratice.student.entity.Course;
import com.pratice.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s.courses FROM Student s WHERE s.id = :studentId")
    List<Course> findCourseById(@Param("studentId") Long studentId);

    Student findStudentById(Long studentId);
}
