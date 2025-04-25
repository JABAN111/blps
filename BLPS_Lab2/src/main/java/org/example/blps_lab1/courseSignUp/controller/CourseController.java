package org.example.blps_lab1.courseSignUp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.courseSignUp.dto.CourseDto;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@AllArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses(){
        Map<String, Object> response = new HashMap<>();
        List<Course> courseList = courseService.getAllCourses();
        List<CourseDto> courseDtoList = courseService.convertToDto(courseList);
        response.put("course_list", courseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable UUID uuid){
        Map<String, Object> response = new HashMap<>();
        Course course = courseService.getCourseByUUID(uuid);
        CourseDto courseDto = courseService.convertToDto(course);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createCourse(@Valid @RequestBody Course course){
        Map<String, Object> response = new HashMap<>();
        Course newCourse = courseService.createCourse(course);
        CourseDto courseDto = courseService.convertToDto(newCourse);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{courseUUID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable UUID courseUUID){
        Map<String, Object> response = new HashMap<>();
        courseService.deleteCourse(courseUUID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseUUID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable UUID courseUUID, @Valid @RequestBody CourseDto courseDto){
        Map<String, Object> response = new HashMap<>();
        Course updatedCourse = courseService.updateCourse(courseUUID, courseDto);
        response.put("message", "course updated");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{courseId}/additional/{additionalId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> addAdditionalCourse(
            @PathVariable UUID courseId,
            @PathVariable UUID additionalId
    ){
        Map<String, Object> response = new HashMap<>();
        Course updatedCourse = courseService.addAdditionalCourses(courseId, additionalId);
        response.put("message", "Дополнительный курс добавлен");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseUUID}/additional-courses")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Course> addListOfCourses(@PathVariable UUID courseUUID, @RequestBody List<Course> additionalCourses){
        Course updatedCourse = courseService.addListOfCourses(courseUUID, additionalCourses);
        return ResponseEntity.ok(updatedCourse);
    }
}
