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

    List<Student> findByCoursesCourseId(Long courseId);

    @Query("SELECT s FROM Student s JOIN s.course c WHERE c.courseId = :courseId")
    List<Student> findByCourses_CourseId(@Param("courseId") Long courseId);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses")
    List<Student> findAllWithCourse();

    @Query("SELECT s.c")
    List<Course> findWithCoursesById();
}
