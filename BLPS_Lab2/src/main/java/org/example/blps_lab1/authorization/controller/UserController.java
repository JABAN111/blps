package org.example.blps_lab1.authorization.controller;


import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.service.impl.ApplicationService;
import org.example.blps_lab1.authorization.service.impl.UserEnrollmentService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserEnrollmentService userEnrollmentService;
    private ApplicationService applicationService;


    /**
     * Данный endpoint создает заявку, ПРИВЯЗАННУЮ К КОНКРЕТНОМУ ПОЛЬЗОВАТЕЛЮ
     * пользователь достается из jwt токена
     */
    @PostMapping("/application/{courseId}")
    public void createApplication(@PathVariable UUID courseUUID) {
        log.info("got request for course with id: {}", courseUUID);
        applicationService.add(courseUUID);
    }

    /**
     *
     * @param id
     * @param status
     */
    @PatchMapping("/application/status/{id}")
    public void updateApplicationStatus(@PathVariable Long id, @RequestBody Map<String, String> status) {
        try {
            String appStatus = status.get("status");
            ApplicationStatus applicationStatus = ApplicationStatus.valueOf(appStatus.toUpperCase().trim());
            userEnrollmentService.processEnrolment(id, applicationStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Статус указан неверно");
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Нельзя изменить статус уже сформированной заявкия");
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

}
