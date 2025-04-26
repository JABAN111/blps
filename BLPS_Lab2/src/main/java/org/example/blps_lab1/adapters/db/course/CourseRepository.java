package org.example.blps_lab1.adapters.db.course;

import org.example.blps_lab1.core.domain.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    Course findByCourseName(String courseName);

}
