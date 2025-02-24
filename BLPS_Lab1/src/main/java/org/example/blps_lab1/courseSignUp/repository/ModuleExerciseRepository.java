package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.courseSignUp.models.ModuleExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleExerciseRepository extends JpaRepository<ModuleExercise, Long> {
    List<ModuleExercise> findByModuleId(Long id);
}
