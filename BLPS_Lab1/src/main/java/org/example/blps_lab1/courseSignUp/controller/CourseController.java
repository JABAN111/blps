package org.example.blps_lab1.courseSignUp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.courseSignUp.dto.CourseDto;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@AllArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses(){
        Map<String, Object> response = new HashMap<>();
        response.put("expedition_list", courseService.getAllCourses());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(@Valid @RequestBody Course course){
        Map<String, Object> response = new HashMap<>();
        courseService.createCourse(course);
        response.put("expedition", course);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> deleteExpedition(@PathVariable Long courseId){
        Map<String, Object> response = new HashMap<>();
        courseService.deleteCourse(courseId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseDto courseDto){
        Map<String, Object> response = new HashMap<>();
        Course updatedCourse = courseService.updateCourse(courseId, courseDto);
        response.put("message", "course updated");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
