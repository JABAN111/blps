package org.example.blps_lab1.courseSignUp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.repository.UserRepository;
import org.example.blps_lab1.courseSignUp.models.CourseProgress;
import org.example.blps_lab1.courseSignUp.repository.CourseProgressRepository;
import org.example.blps_lab1.courseSignUp.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseProgressService {

    private final CourseProgressRepository courseProgressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void addPoints(Long userId, Long courseId, int points){
        CourseProgress progress = courseProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> {
                   CourseProgress newProgress = new CourseProgress();
                   newProgress.setUserId(userId);
                   newProgress.setCourseId(courseId);
                   newProgress.setEarnedPoints(points);
                   return newProgress;
                });

        progress.setEarnedPoints(progress.getEarnedPoints() + points);
        courseProgressRepository.save(progress);
        log.info("User {} earned {} points for course {}", userId, points, courseId);

    }
}

