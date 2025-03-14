package org.example.blps_lab1.authorization.controller;


import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.service.impl.ApplicationService;
import org.example.blps_lab1.authorization.service.impl.UserEnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private UserEnrollmentService userEnrollmentService;
    private ApplicationService applicationService;


    @PostMapping("/application/{courseId}")
    public void createApplication(@PathVariable Long courseId){
        applicationService.save(courseId);
    }
    
    @PatchMapping("/application/status/{id}")
    public void updateApplicationStatus(@PathVariable Long id, @RequestBody Map<String, String> status) {

        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase().trim());
        userEnrollmentService.processEnrolment(id, applicationStatus);
    }

    @GetMapping("/ping")
    public String ping(){
        return "Pong";
    }

}
