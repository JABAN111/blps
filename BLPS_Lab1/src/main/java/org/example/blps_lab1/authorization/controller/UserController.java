package org.example.blps_lab1.authorization.controller;


import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.service.impl.ApplicationService;
import org.example.blps_lab1.authorization.service.impl.UserEnrollmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

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
    
    @PutMapping("/application/{id}/{status}")
    public void updateApplicationStatus(@PathVariable Long id, @PathVariable String status) {
        userEnrollmentService.processEnrolment(id, ApplicationStatus.valueOf(status.toUpperCase().trim()));
    }

    @GetMapping("/ping")
    public String ping(){
        return "Pong";
    }

}
