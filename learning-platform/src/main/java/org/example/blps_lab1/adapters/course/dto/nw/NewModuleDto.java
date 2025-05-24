package org.example.blps_lab1.adapters.course.dto.nw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class NewModuleDto {
    private UUID uuid;
    private String name;
    private String description;
    private List<NewExerciseDto> exercises;
}
