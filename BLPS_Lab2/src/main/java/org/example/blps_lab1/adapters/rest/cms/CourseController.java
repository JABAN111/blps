package org.example.blps_lab1.adapters.rest.cms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Course-controller", description = "Контроллер для управления курсами")
public class CourseController {
    private final CourseService courseService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    @Operation(summary = "Создание курса")
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();

        Course newCourse = courseService.createCourse(course);
        CourseDto courseDto = CourseMapper.toDto(newCourse);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{courseUUID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Удаление курса")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable @Parameter(description = "Идентификатор курса") Long courseUUID) {
        Map<String, Object> response = new HashMap<>();
        courseService.deleteCourse(courseUUID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseUUID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Обновление курса")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable @Parameter(description = "Идентификатор курса") Long courseUUID, @Valid @RequestBody CourseDto courseDto) {
        Map<String, Object> response = new HashMap<>();
        Course updatedCourse = courseService.updateCourse(courseUUID, courseDto);
        response.put("message", "course updated");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{courseId}/additional/{additionalId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Привязка дополнительного курса")
    public ResponseEntity<Map<String, Object>> addAdditionalCourse(
            @PathVariable @Parameter(description = "Индентификатор основоного курса") Long courseId,
            @PathVariable @Parameter(description = "Идентификатор дополнительного курса") Long additionalId
    ) {
        Map<String, Object> response = new HashMap<>();
        Course updatedCourse = courseService.addAdditionalCourses(courseId, additionalId);
        response.put("message", "Дополнительный курс добавлен");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseUUID}/additional-courses")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Привязка спика дополнительных курсов")
    public ResponseEntity<Course> addListOfCourses(@PathVariable @Parameter(description = "Индентификатор основного курса") Long courseUUID, @RequestBody @Parameter(description = "список курсов для привязки") List<Course> additionalCourses) {
        Course updatedCourse = courseService.addListOfCourses(courseUUID, additionalCourses);
        return ResponseEntity.ok(updatedCourse);
    }
}
