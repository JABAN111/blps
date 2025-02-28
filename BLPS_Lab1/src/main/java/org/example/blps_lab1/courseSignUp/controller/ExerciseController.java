package org.example.blps_lab1.courseSignUp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.blps_lab1.courseSignUp.dto.ExerciseDto;
import org.example.blps_lab1.courseSignUp.models.Exercise;
import org.example.blps_lab1.courseSignUp.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exercises")
@AllArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExercises(){
        Map<String, Object> response = new HashMap<>();
        response.put("exercise_list", exerciseService.getAllExercises());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getExerciseById(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        response.put("exercise", exerciseService.getExerciseById(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createExercise(@Valid @RequestBody ExerciseDto exerciseDto){
        Map<String, Object> response = new HashMap<>();
        Exercise createdExercise = exerciseService.createExercise(exerciseDto);
        response.put("created_exercise", createdExercise);
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

    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitAnswer(@PathVariable Long id, @RequestParam String userAnswer){
        boolean isCorrect = exerciseService.submitAnswer(id, userAnswer);
        Map<String, Object> response = new HashMap<>();
        response.put("exercise_id", id);
        response.put("is_correct", isCorrect);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
