package org.example.blps_lab1.adapters.auth.service;

import org.example.blps_lab1.core.ports.auth.UserEnrollmentService;
import org.example.blps_lab1.core.domain.auth.ApplicationStatus;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.ports.auth.UserService;

import org.example.blps_lab1.core.ports.course.nw.NewCourseService;
import org.example.blps_lab1.core.ports.email.EmailService;
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
public class UserEnrollmentServiceImpl implements UserEnrollmentService {
    private final ApplicationServiceImpl applicationService;
    private final UserService userService;
    private final AuthService authService;
    private final NewCourseService courseService;
    private final EmailService emailService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public UserEnrollmentServiceImpl(
            PlatformTransactionManager transactionTemplate, EmailService emailService,
            NewCourseService courseService, AuthService authService,
            UserService userService, ApplicationServiceImpl applicationService) {
        this.transactionTemplate = new TransactionTemplate(transactionTemplate);
        this.emailService = emailService;
        this.courseService = courseService;
        this.authService = authService;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    @Override
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
                    emailService.rejectionMail(authService.getCurrentUser().getUsername(), applicationEntity.getNewCourse().getName());
                    return;
                }
                var courseUUID = applicationEntity.getNewCourse().getUuid();
                var user = authService.getCurrentUser();
                userService.enrollUser(user, applicationEntity.getNewCourse());
                courseService.enrollStudent(user.getId(), courseUUID);
            }
        });
    }
}

