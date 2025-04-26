package org.example.blps_lab1.adapters.rest.cms;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.blps_lab1.adapters.course.dto.ExerciseDto;
import org.example.blps_lab1.adapters.course.mapper.ExerciseMapper;
import org.example.blps_lab1.core.domain.course.Exercise;
import org.example.blps_lab1.core.ports.course.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exercises")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createExercise(@Valid @RequestBody ExerciseDto exerciseDto){
        Map<String, Object> response = new HashMap<>();
        Exercise createdExercise = exerciseService.createExercise(exerciseDto);
        ExerciseDto newExerciseDto = ExerciseMapper.convertToExerciseDto(createdExercise);
        response.put("created_exercise", newExerciseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteExercise(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        exerciseService.deleteExercise(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateExercise(@PathVariable Long id, @Valid @RequestBody ExerciseDto exerciseDto){
        Map<String, Object> response = new HashMap<>();
        Exercise updatedExercise = exerciseService.updateExercise(id, exerciseDto);
        response.put("message", "exercise updated");
        response.put("exercise", updatedExercise);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
