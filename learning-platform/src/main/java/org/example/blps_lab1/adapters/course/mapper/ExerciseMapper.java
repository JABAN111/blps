//package org.example.blps_lab1.adapters.course.mapper;
//
//import org.example.blps_lab1.adapters.course.dto.ExerciseDto;
//import org.example.blps_lab1.core.domain.course.Exercise;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ExerciseMapper {
//    public static List<ExerciseDto> convertToExerciseDto(List<Exercise> exercises) {
//        return exercises.stream()
//                .map(ExerciseMapper::convertToExerciseDto)
//                .collect(Collectors.toList());
//    }
//
//    public static ExerciseDto convertToExerciseDto(Exercise exercise) {
//        Long moduleId = (exercise.getModuleExercises() != null && !exercise.getModuleExercises().isEmpty())
//                ? exercise.getModuleExercises().get(0).getModule().getId() : null;
//
//        return new ExerciseDto(
//                exercise.getName(),
//                exercise.getDescription(),
//                moduleId,
//                exercise.getDifficultyLevel(),
//                exercise.getAnswer(),
//                exercise.getLocalDateTime()
//        );
//    }
//}
