package org.example.blps_lab1.courseSignUp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.UserRepository;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.models.CourseProgress;
import org.example.blps_lab1.courseSignUp.models.CourseProgressId;
import org.example.blps_lab1.courseSignUp.repository.CourseProgressRepository;
import org.example.blps_lab1.courseSignUp.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseProgressService {

    private final CourseRepository courseRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final UserRepository userRepository;

    public void addPoints(Long userId, Long courseId, int points){
        CourseProgressId courseProgressId = new CourseProgressId(courseId, userId);
        CourseProgress progress = courseProgressRepository.findByCourseProgressId(courseProgressId)
                .orElseGet(() -> {
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new EntityNotFoundException("course not found in add points"));
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден в addPoints"));
                    CourseProgress newProgress = new CourseProgress();
                    newProgress.setCourseProgressId(courseProgressId);
                    newProgress.setCourse(course);
                    newProgress.setUser(user);
                    newProgress.setEarnedPoints(0);
                    return newProgress;
                });

        progress.setEarnedPoints(progress.getEarnedPoints() + points);
        courseProgressRepository.save(progress);
        log.info("User {} earned {} points for course {}", userId, points, courseId);
    }
}

