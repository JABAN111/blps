package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.models.CourseProgress;
import org.example.blps_lab1.courseSignUp.models.CourseProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, CourseProgressId> {
    Optional<CourseProgress> findByCourseProgressId(CourseProgressId courseProgressId);
    Optional<CourseProgress> findByUserAndCourse(User user, Course course);
}
