package org.example.blps_lab1.adapters.db.course;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.course.Exercise;
import org.example.blps_lab1.core.domain.course.UserExerciseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserExerciseProgressRepository extends JpaRepository<UserExerciseProgress, Long> {
    Optional<UserExerciseProgress> findByUserAndExercise(User user, Exercise exercise);
}

