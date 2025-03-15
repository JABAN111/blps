package org.example.blps_lab1.authorization.controller;


import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.service.impl.ApplicationService;
import org.example.blps_lab1.authorization.service.impl.UserEnrollmentService;

import org.springframework.web.bind.annotation.*;

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
    
    @PatchMapping("/application/status/{id}")
    public void updateApplicationStatus(@PathVariable Long id, @RequestBody Map<String, String> status) {
        try{
            String appStatus = status.get("status");
            ApplicationStatus applicationStatus = ApplicationStatus.valueOf(appStatus.toUpperCase().trim());
            userEnrollmentService.processEnrolment(id, applicationStatus);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Статус указан неверно");
        }catch (IllegalStateException e){
            throw new IllegalArgumentException("Нельзя изменить статус уже сформированной заявкия");
        }


    @GetMapping("/ping")
    public String ping(){
        return "Pong";
    }

}
