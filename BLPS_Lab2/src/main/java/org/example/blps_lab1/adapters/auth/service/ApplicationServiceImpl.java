package org.example.blps_lab1.adapters.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.example.blps_lab1.core.ports.auth.ApplicationService;
import org.example.blps_lab1.core.domain.auth.Application;
import org.example.blps_lab1.core.domain.auth.ApplicationStatus;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.adapters.db.auth.ApplicationRepository;
import org.example.blps_lab1.core.ports.auth.UserService;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.example.blps_lab1.core.exception.auth.ApplicationStatusAlreadySetException;
import org.example.blps_lab1.core.ports.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
//transactional OK
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository repository;
    private final UserService userService;
    private final CourseService courseService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository repository, UserService userService, CourseService courseService, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.userService = userService;
        this.courseService = courseService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public Application add(UUID courseUUID) {
        var userEntity = getCurrentUser();
        return add(courseUUID, userEntity);
    }

    @Override
    public Application add(UUID courseUUID, User user) {
        return transactionTemplate.execute(status -> {
            var courseEntity = courseService.find(courseUUID);
            var app = Application.builder()
                    .course(courseEntity)
                    .user(user)
                    .status(ApplicationStatus.PENDING)
                    .build();
            log.debug("attempt to create application: {}", app);
            return repository.save(app);
        });
    }

    @Override
    public Application updateStatus(Long id, ApplicationStatus applicationStatus) {
        return transactionTemplate.execute(status -> {
            Optional<Application> oldEntityOptional = repository.findById(id);
            if (oldEntityOptional.isEmpty()) {
                log.warn("Application with id: {} did not exist", id);
                throw new ObjectNotExistException("Заявки с id: " + id + "  не существует");
            }
            var entity = oldEntityOptional.get();
            if (entity.getStatus() != ApplicationStatus.PENDING) {
                throw new ApplicationStatusAlreadySetException("Нельзя изменить статус уже сформированной заявки");
            }
            entity.setStatus(applicationStatus);

            return repository.save(entity);
        });
    }

    private User getCurrentUser() {
//        copypaste cause depend cycle
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userService.getUserByEmail(username);
        } else {
            throw new IllegalStateException("Current user is not authenticated");
        }
    }

}
