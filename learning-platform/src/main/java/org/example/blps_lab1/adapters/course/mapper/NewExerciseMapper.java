package org.example.blps_lab1.adapters.course.mapper;

import org.example.blps_lab1.adapters.course.dto.nw.NewExerciseDto;
import org.example.blps_lab1.core.domain.course.nw.NewExercise;

public class NewExerciseMapper {

    public static NewExercise toEntity(NewExerciseDto dto) {
        return NewExercise.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .answer(dto.getAnswer())
                .points(dto.getPoints())
                // NOTE: difficulty level was skipped
                .build();
    }

    public static NewExerciseDto toDto(NewExercise entity) {
        return NewExerciseDto.builder()
                .uuid(entity.getUuid())
                .name(entity.getName())
                .description(entity.getDescription())
                .answer(entity.getAnswer())
                .points(entity.getPoints())
                .build();
    }
}
