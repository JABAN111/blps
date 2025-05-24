package org.example.blps_lab1.adapters.rest.lms;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.adapters.course.mapper.NewCourseMapper;
import org.example.blps_lab1.core.ports.course.nw.NewCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("lmsCourseController")
@RequestMapping("/api/v1/lms/courses")
@AllArgsConstructor
@Slf4j
public class CourseController {
    private final NewCourseService courseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        Map<String, Object> response = new HashMap<>();
        var courseList = courseService.getAllCourses();
        var toRet = courseList
                .stream()
                .map(NewCourseMapper::toDto)
                .toList();
        response.put("course_list", toRet);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable UUID uuid) {
        Map<String, Object> response = new HashMap<>();
        var course = courseService.getCourseByUUID(uuid);
        var courseDto = NewCourseMapper.toDto(course);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/complete/{courseUUID}")
    public ResponseEntity<Map<String, Object>> completeCourse(@PathVariable UUID courseUUID) {
        var isFinished = courseService.isCourseFinished(courseUUID);
        Map<String, Object> response = new HashMap<>();
        response.put("courseUUID", courseUUID);
        response.put("isFinished", isFinished);
        if(isFinished)
            response.put("message", "Курс успешно завершён, можете запросить сертификат");
        else
            response.put("message", "Курс еще не завершён");
        return ResponseEntity.ok(response);
    }

}
