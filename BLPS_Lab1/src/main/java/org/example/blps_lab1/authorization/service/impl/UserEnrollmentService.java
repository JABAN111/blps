package org.example.blps_lab1.authorization.service.impl;

import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.authorization.service.UserService;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @Slf4j @AllArgsConstructor @Transactional
public class UserEnrollmentService {
    private ApplicationService applicationService;
    private UserService userService;
    private AuthService authService;


    //TODO: В дополнении БПМН была следующая просьба: уведомлять пользователя в случае успешного отказа в обработке
    // ну и сюда нужно, естественно, еще добавить отправку на почту об успешном зачислении
    public void processEnrolment(Long applicationEnrollmentId, ApplicationStatus status){
        var applicationEntity = applicationService.updateStatus(applicationEnrollmentId, status);
        if (status == ApplicationStatus.REJECT){
            return;//TODO: process mailing
        }        

        var user = authService.getCurrentUser();
        userService.enrollUser(user, applicationEntity.getCourse());
        //TODO: sending an email...
    }

}

