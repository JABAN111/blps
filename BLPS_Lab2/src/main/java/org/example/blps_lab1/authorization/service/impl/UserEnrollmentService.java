package org.example.blps_lab1.authorization.service.impl;

import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.authorization.service.UserService;

import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.example.blps_lab1.lms.service.EmailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class UserEnrollmentService {
    private final ApplicationService applicationService;
    private final UserService userService;
    private final AuthService authService;
    private final CourseService courseService;
    private final EmailService emailService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public UserEnrollmentService(PlatformTransactionManager transactionTemplate, EmailService emailService, CourseService courseService, AuthService authService, UserService userService, ApplicationService applicationService) {
        this.transactionTemplate = new TransactionTemplate(transactionTemplate);
        this.emailService = emailService;
        this.courseService = courseService;
        this.authService = authService;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    public void processEnrolment(Long applicationEnrollmentId, String applicationStatus) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                ApplicationStatus appStatus;
                try {
                    appStatus = ApplicationStatus.valueOf(applicationStatus.toUpperCase().trim());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Статус указан неверно");
                } catch (IllegalStateException e) {
                    throw new IllegalArgumentException("Нельзя изменить статус уже сформированной заявки");
                }
                var applicationEntity = applicationService.updateStatus(applicationEnrollmentId, appStatus);
                if (appStatus == ApplicationStatus.REJECT) {
                    emailService.rejectionMail(authService.getCurrentUser().getEmail(), applicationEntity.getCourse().getCourseName());
                    return;
                }
                var courseUUID = applicationEntity.getCourse().getCourseUUID();
                var user = authService.getCurrentUser();
                userService.enrollUser(user, applicationEntity.getCourse());
                courseService.enrollUser(user.getId(), courseUUID);
            }
        });
    }

}

