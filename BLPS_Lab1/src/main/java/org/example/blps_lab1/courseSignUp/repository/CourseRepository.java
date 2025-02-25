package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.courseSignUp.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findByCourseName(String courseName);

}
