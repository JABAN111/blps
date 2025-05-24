package org.example.blps_lab1.adapters.rest.lms;

import lombok.AllArgsConstructor;
import org.example.blps_lab1.adapters.course.mapper.NewExerciseMapper;
import org.example.blps_lab1.core.ports.course.nw.NewExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("lmsEnrollmentController")
@RequestMapping("/api/v1/lms/exercises")
@AllArgsConstructor
public class ExerciseController {
    private final NewExerciseService exerciseService;

    @PostMapping("/{uuid}/submit")
    public ResponseEntity<Map<String, Object>> submitAnswer(@PathVariable UUID uuid, @RequestBody Map<String, String> userAnswer){
        String answer = userAnswer.get("answer");
        Boolean isCorrect = exerciseService.submitAnswer(uuid, answer);
        Map<String, Object> response = new HashMap<>();
        response.put("exercise_id", uuid);
        response.put("is_correct", isCorrect);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
