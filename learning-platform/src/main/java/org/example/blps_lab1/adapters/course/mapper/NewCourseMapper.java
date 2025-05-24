package org.example.blps_lab1.adapters.course.mapper;

import org.example.blps_lab1.adapters.course.dto.nw.NewCourseDto;
import org.example.blps_lab1.adapters.course.dto.nw.NewModuleDto;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.example.blps_lab1.core.domain.course.nw.NewModule;

import java.util.ArrayList;
import java.util.List;

public class NewCourseMapper {

    public static NewCourseDto toDto(NewCourse entity) {
        NewCourseDto dto = new NewCourseDto();
        dto.setUuid(entity.getUuid());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setCreationTime(entity.getCreationTime());
        dto.setTopic(entity.getTopic());

        List<NewModuleDto> moduleDtos = new ArrayList<>();
        if (entity.getNewModuleList() != null) {
            for (NewModule module : entity.getNewModuleList()) {
                moduleDtos.add(NewModuleMapper.toDto(module));
            }
        }
        dto.setNewModuleList(moduleDtos);

        return dto;
    }

    public static NewCourse toEntity(NewCourseDto dto) {
        NewCourse entity = new NewCourse();
        entity.setUuid(dto.getUuid());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setCreationTime(dto.getCreationTime());
        entity.setTopic(dto.getTopic());

        List<NewModule> modules = new ArrayList<>();
        if (dto.getNewModuleList() != null) {
            for (NewModuleDto modDto : dto.getNewModuleList()) {
                modules.add(NewModuleMapper.toEntity(modDto));
            }
        }
        entity.setNewModuleList(modules);

        return entity;
    }
}
