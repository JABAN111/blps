package org.example.blps_lab1.adapters.course.mapper;

import org.example.blps_lab1.adapters.course.dto.nw.NewModuleDto;
import org.example.blps_lab1.core.domain.course.nw.NewModule;


public class NewModuleMapper {

    public static NewModule toEntity(NewModuleDto dto) {
        return NewModule.builder()
                .uuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public static NewModuleDto toDto(NewModule entity) {
        var exerciseList = entity.getExercises() == null ? null : entity.getExercises()
                .stream()
                .map(NewExerciseMapper::toDto)
                .toList();
        return NewModuleDto.builder()
                .uuid(entity.getUuid())
                .name(entity.getName())
                .description(entity.getDescription())
                .exercises(exerciseList)
                .build();
    }
}
