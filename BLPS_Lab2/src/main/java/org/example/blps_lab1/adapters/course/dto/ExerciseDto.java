package org.example.blps_lab1.adapters.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.core.domain.course.DifficultyLevel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private String name;
    private String description;
    private Long moduleId;
    private DifficultyLevel difficultyLevel;
    private String answer;
    private LocalDateTime localDateTime;
}
