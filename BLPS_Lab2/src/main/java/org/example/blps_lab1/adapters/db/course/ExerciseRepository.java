package org.example.blps_lab1.adapters.db.course;

import org.example.blps_lab1.core.domain.course.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
