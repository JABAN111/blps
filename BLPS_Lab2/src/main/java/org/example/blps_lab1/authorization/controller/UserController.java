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
    @PostMapping("/application/{courseUUID}")
    public void createApplication(@PathVariable UUID courseUUID) {
        log.info("got request for course with id: {}", courseUUID);
        applicationService.add(courseUUID);
    }

    /**
     * Данный endpoint обновляет заявку, ПРИВЯЗАННУЮ К КОНКРЕТНОМУ ПОЛЬЗОВАТЕЛЮ.
     *
     * @param applicationID идентификатор заявки
     * @param body          тело Patch-запроса с новым статусом заявки.
     *                      Заявка имеет три статуса, соответствующих enum {@link ApplicationStatus}:
     *                      <pre>{@code
     *                                  {
     *                                      OK,
     *                                      REJECT,
     *                                      PENDING
     *                                  }
     *                                  }</pre>
     */
    @PatchMapping("/application/status/{applicationID}")
    //FIXME вынести парсинг из endpoint в бизнес логику. Туда просто передавать в виде строки
    public void updateApplicationStatus(@PathVariable Long applicationID, @RequestBody Map<String, String> body) {
        try {
            String appStatus = body.get("newStatus");
            ApplicationStatus applicationStatus = ApplicationStatus.valueOf(appStatus.toUpperCase().trim());
            userEnrollmentService.processEnrolment(applicationID, applicationStatus);
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
