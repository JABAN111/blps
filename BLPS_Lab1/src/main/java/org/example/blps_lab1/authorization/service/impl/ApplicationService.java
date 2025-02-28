package org.example.blps_lab1.authorization.service.impl;

import java.util.Optional;

import org.example.blps_lab1.authorization.models.Application;
import org.example.blps_lab1.authorization.models.ApplicationStatus;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.ApplicationRepository;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @Transactional @Slf4j @AllArgsConstructor
public class ApplicationService {
    private ApplicationRepository repository;
    private UserService userService;
    private CourseService courseService;


    public Application save(Long courseId){
        var userEntity = getCurrentUser();
        return save(courseId, userEntity);
    }

    public Application save(Long courseId, User user){
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

    public Application updateStatus(Long id, ApplicationStatus status){
        Optional<Application> oldEntityOptional = repository.findById(id);
        if(oldEntityOptional.isEmpty()){
            log.warn("Application with id: {} did not exist", id);
            throw new ObjectNotExistException("Заявки с id: " + id + "  не существует");
        }
        var entity = oldEntityOptional.get();
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
