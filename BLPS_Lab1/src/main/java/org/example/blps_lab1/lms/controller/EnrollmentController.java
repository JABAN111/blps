package org.example.blps_lab1.lms.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.service.CourseService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/enrollment")
@AllArgsConstructor
public class EnrollmentController {

    private final CourseService courseService;

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Course> enrollUser(@RequestParam Long userId, Long courseId){
        return courseService.enrollUser(userId, courseId);
    }

}
