package org.example.blps_lab1.adapters.rest.cms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.adapters.course.dto.nw.NewCourseDto;
import org.example.blps_lab1.adapters.course.mapper.NewCourseMapper;
import org.example.blps_lab1.core.ports.course.nw.NewCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("cmsCourseController")
@RequestMapping("/api/v1/cms/courses")
@AllArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Course-controller", description = "Контроллер для управления курсами")
public class CourseController {
    private final NewCourseService newCourseService;

    @PostMapping
    @Operation(summary = "Создание курса")
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody NewCourseDto dto) {
        Map<String, Object> response = new HashMap<>();
        var newCourse = newCourseService.createCourse(dto);
        var courseDto = NewCourseMapper.toDto(newCourse);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{courseUUID}")
    @Operation(summary = "Удаление курса")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable @Parameter(description = "Идентификатор курса") UUID courseUUID) {
        Map<String, Object> response = new HashMap<>();
        log.debug("got reg to delete");
        newCourseService.deleteCourse(courseUUID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{courseUUID}")
    @Operation(summary = "Обновление курса")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable @Parameter(description = "Идентификатор курса") UUID courseUUID, @RequestBody NewCourseDto courseDto) {
        Map<String, Object> response = new HashMap<>();
        var updatedCourse = newCourseService.updateCourse(courseUUID, courseDto);
        response.put("message", "course updated");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{courseUUID}/additional/{additionalUUID}")
    @Operation(summary = "Привязка дополнительного курса")
    public ResponseEntity<Map<String, Object>> addAdditionalCourse(
            @PathVariable @Parameter(description = "Индентификатор основоного курса") UUID courseUUID,
            @PathVariable @Parameter(description = "Идентификатор дополнительного курса") UUID additionalUUID
    ) {
        Map<String, Object> response = new HashMap<>();
        var updatedCourse = newCourseService.addAdditionalCourses(courseUUID, additionalUUID);
        response.put("message", "Дополнительный курс добавлен");
        response.put("course", updatedCourse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/link")
    @Operation(summary = "привязывает упражнение к модулю")
    public ResponseEntity<Map<String, Object>> linkExerciseToModule(@RequestParam UUID courseUUID, @RequestParam UUID moduleUUID) {
        Map<String, Object> response = new HashMap<>();
        var toRet = NewCourseMapper.toDto(newCourseService.linkModule(courseUUID, moduleUUID));
        response.put("update_course", toRet);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
