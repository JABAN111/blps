package org.example.blps_lab1.authorization.service.impl;

import java.util.Optional;

import org.example.blps_lab1.authorization.models.Application;
import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.ApplicationRepository;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Transactional
@Slf4j
public class ApplicationService {
    private ApplicationRepository repository;
    private UserService userService;
    private CourseService courseService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public ApplicationService(ApplicationRepository repository, UserService userService, CourseService courseService, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.userService = userService;
        this.courseService = courseService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Application add(Long courseId) {
        var userEntity = getCurrentUser();
        return add(courseId, userEntity);
    }

    public Application add(Long courseId, User user) {
        Application application = transactionTemplate.execute(new TransactionCallback<Application>() {
            @Override
            public Application doInTransaction(@NotNull TransactionStatus status) {
                var courseEntity = courseService.find(courseId);
                var app = Application.builder()
                        .course(courseEntity)
                        .user(user)
                        .status(ApplicationStatus.PENDING)
                        .build();
                log.debug("attempt to create application: {}", app);
                return repository.save(
                        app
                );
            }
        });
        return application;
    }




public Application updateStatus(Long id, ApplicationStatus status) {
    Optional<Application> oldEntityOptional = repository.findById(id);
    if (oldEntityOptional.isEmpty()) {
        log.warn("Application with id: {} did not exist", id);
        throw new ObjectNotExistException("Заявки с id: " + id + "  не существует");
    }
    var entity = oldEntityOptional.get();
    if (entity.getStatus() != ApplicationStatus.PENDING) {
        throw new IllegalStateException("Нельзя изменить статус уже сформированной заявки");
    }
    entity.setStatus(status);

    return repository.save(entity);
}

private User getCurrentUser() {
    //copypaste cause depend cycle
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof UserDetails) {
        String username = ((UserDetails) principal).getUsername();

        return userService.getUserByEmail(username);
    } else {
        throw new IllegalStateException("Current user is not authenticated");
    }
}

}
