package org.example.blps_lab1.adapters.course.dto.nw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewExerciseDto {
    private UUID uuid;
    private String name;
    private String description;
    private String answer;
    private Integer points;
}
