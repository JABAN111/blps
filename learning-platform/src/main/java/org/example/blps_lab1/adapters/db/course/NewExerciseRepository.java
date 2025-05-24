package org.example.blps_lab1.adapters.db.course;


import org.example.blps_lab1.core.domain.course.nw.NewExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NewExerciseRepository extends JpaRepository<NewExercise, UUID> {
}
