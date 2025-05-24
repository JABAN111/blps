//package org.example.blps_lab1.adapters.course.mapper;
//
//import org.example.blps_lab1.adapters.course.dto.ModuleDto;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ModuleMapper {
//
//    public static List<ModuleDto> toDto(List<org.example.blps_lab1.core.domain.course.Module> modules){
//        return modules.stream()
//                .map(ModuleMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    public static ModuleDto toDto(Module module){
//        return new ModuleDto(
//                module.getName(),
//                module.getIsCompleted(),
//                module.getOrderNumber(),
//                module.getDescription(),
//                module.getIsBlocked(),
//                module.getTotalPoints(),
//                module.getLocalDateTime()
//        );
//    }
//}
