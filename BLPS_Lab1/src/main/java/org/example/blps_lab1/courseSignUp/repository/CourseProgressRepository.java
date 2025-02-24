package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.courseSignUp.models.CourseProgress;
import org.example.blps_lab1.courseSignUp.models.CourseProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, CourseProgressId> {
    Optional<CourseProgress> findByUserIdAndCourseId(Long userId, Long courseId);
}
