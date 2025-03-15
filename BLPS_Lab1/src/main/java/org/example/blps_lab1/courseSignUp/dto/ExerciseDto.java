package org.example.blps_lab1.courseSignUp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.courseSignUp.models.DifficultyLevel;

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
