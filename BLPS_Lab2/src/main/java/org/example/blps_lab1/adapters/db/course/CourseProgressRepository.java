package org.example.blps_lab1.adapters.db.course;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.course.Course;
import org.example.blps_lab1.core.domain.course.CourseProgress;
import org.example.blps_lab1.core.domain.course.CourseProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, CourseProgressId> {
    Optional<CourseProgress> findByCourseProgressId(CourseProgressId courseProgressId);
    Optional<CourseProgress> findByUserAndCourse(User user, Course course);
}
