package org.example.blps_lab1.courseSignUp.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.blps_lab1.courseSignUp.repository.CourseProgressRepository;
import org.example.blps_lab1.courseSignUp.service.CourseProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class CourseProgressController {
    private final CourseProgressService courseProgressService;

    @PostMapping("/add-points")
    public ResponseEntity<String> addPoints(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam int points
    ){
        courseProgressService.addPoints(userId, courseId, points);
        return ResponseEntity.ok("Points successfully added");
    }
}

