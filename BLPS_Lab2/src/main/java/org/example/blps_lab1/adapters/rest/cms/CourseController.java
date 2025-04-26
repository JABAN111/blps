package org.example.blps_lab1.adapters.rest.cms;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.adapters.course.dto.CourseDto;
import org.example.blps_lab1.adapters.course.mapper.CourseMapper;
import org.example.blps_lab1.core.domain.course.Course;
import org.example.blps_lab1.core.ports.course.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController("cmsCourseController")
@RequestMapping("/api/v1/courses")
@AllArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();

        Course newCourse = courseService.createCourse(course);
        CourseDto courseDto = CourseMapper.toDto(newCourse);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{courseUUID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable UUID courseUUID) {
        Map<String, Object> response = new HashMap<>();
        courseService.deleteCourse(courseUUID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseUUID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable UUID courseUUID, @Valid @RequestBody CourseDto courseDto) {
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
    ) {
        Map<String, Object> response = new HashMap<>();
        Course updatedCourse = courseService.addAdditionalCourses(courseId, additionalId);
        response.put("message", "Дополнительный курс добавлен");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseUUID}/additional-courses")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Course> addListOfCourses(@PathVariable UUID courseUUID, @RequestBody List<Course> additionalCourses) {
        Course updatedCourse = courseService.addListOfCourses(courseUUID, additionalCourses);
        return ResponseEntity.ok(updatedCourse);
    }
}
