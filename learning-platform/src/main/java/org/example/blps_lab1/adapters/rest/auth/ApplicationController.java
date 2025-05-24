package org.example.blps_lab1.adapters.rest.auth;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.auth.ApplicationStatus;
import org.example.blps_lab1.adapters.auth.service.ApplicationServiceImpl;
import org.example.blps_lab1.adapters.auth.service.UserEnrollmentServiceImpl;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Slf4j
public class ApplicationController {

    private UserEnrollmentServiceImpl userEnrollmentService;
    private ApplicationServiceImpl applicationService;


    /**
     * Данный endpoint создает заявку,
     * ПРИВЯЗАННУЮ К КОНКРЕТНОМУ ПОЛЬЗОВАТЕЛЮ
     * пользователь достается из jwt токена
     * Возвращает id заявки, которую можно подтвердить или отказаться. См {@code updateApplicationStatus()}
     */
    @PostMapping("/application/{courseUUID}")
    public Long createApplication(@PathVariable UUID courseUUID) {
        log.info("got request for course with id: {}", courseUUID);
        var applicationEntity = applicationService.add(courseUUID);
        return applicationEntity.getId();
    }

    /**
     * Данный endpoint обновляет заявку, ПРИВЯЗАННУЮ К КОНКРЕТНОМУ ПОЛЬЗОВАТЕЛЮ.
     *
     * @param applicationID идентификатор заявки
     * @param body          тело Patch-запроса с новым статусом заявки.
     *                      Заявка имеет три статуса, соответствующих enum {@link ApplicationStatus}:
     *                      <pre>{@code
     *                                                                            {
     *                                                                                OK,
     *                                                                                REJECT,
     *                                                                                PENDING
     *                                                                            }
     *                                                                            }</pre>
     */
    @PatchMapping("/application/status/{applicationID}")
    public void updateApplicationStatus(@PathVariable Long applicationID, @RequestBody Map<String, String> body) {
        String appStatus = body.get("newStatus");
        userEnrollmentService.processEnrolment(applicationID, appStatus);
    }
}
