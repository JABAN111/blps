package org.example.blps_lab1.adapters.rest.lms;

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

@RestController("lmsCourseController")
@RequestMapping("/api/v1/courses")
@AllArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        Map<String, Object> response = new HashMap<>();
        List<Course> courseList = courseService.getAllCourses();
        List<CourseDto> courseDtoList = CourseMapper.toDto(courseList);
        response.put("course_list", courseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Course course = courseService.getCourseByID(id);
        CourseDto courseDto = CourseMapper.toDto(course);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/name/{courseName}")
    public ResponseEntity<Map<String, Object>> getCourseByName(@PathVariable String courseName) {
        Map<String, Object> response = new HashMap<>();
        Course course = courseService.find(courseName);
        CourseDto courseDto = CourseMapper.toDto(course);
        response.put("course", courseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
