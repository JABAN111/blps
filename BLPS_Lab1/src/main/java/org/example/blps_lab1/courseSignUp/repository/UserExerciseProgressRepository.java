package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.courseSignUp.models.Exercise;
import org.example.blps_lab1.courseSignUp.models.UserExerciseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserExerciseProgressRepository extends JpaRepository<UserExerciseProgress, Long> {
    Optional<UserExerciseProgress> findByUserAndExercise(User user, Exercise exercise);
}

