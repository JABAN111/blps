package org.example.blps_lab1.adapters.rest.lms;

import lombok.AllArgsConstructor;
import org.example.blps_lab1.adapters.course.dto.ExerciseDto;
import org.example.blps_lab1.adapters.course.mapper.ExerciseMapper;
import org.example.blps_lab1.core.domain.course.Exercise;
import org.example.blps_lab1.core.ports.course.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("lmsEnrollmentController")
@RequestMapping("/api/v1/exercises")
@AllArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExercises(){
        //TODO если останутся силы и время -> создать Facade для обертки всей этой истории с глаз долой
        Map<String, Object> response = new HashMap<>();
        List<Exercise> exerciseList = exerciseService.getAllExercises();
        List<ExerciseDto> exerciseDtoList = ExerciseMapper.convertToExerciseDto(exerciseList);
        response.put("exercise_list", exerciseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getExerciseById(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        Exercise exercise = exerciseService.getExerciseById(id);
        ExerciseDto exerciseDto = ExerciseMapper.convertToExerciseDto(exercise);
        response.put("exercise", exerciseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitAnswer(@PathVariable Long id, @RequestBody Map<String, String> userAnswer){
        String answer = userAnswer.get("answer");
        Boolean isCorrect = exerciseService.submitAnswer(id, answer);
        Map<String, Object> response = new HashMap<>();
        response.put("exercise_id", id);
        response.put("is_correct", isCorrect);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
