package org.example.blps_lab1.authorization.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.UserRepository;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
// transactional OK
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CourseService courseService, PlatformTransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.courseService = courseService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }


    @Override
    public User add(final User user) {
        return transactionTemplate.execute(status -> {
            user.setPassword(user.getPassword());
            User savedUser = userRepository.save(user);
            log.info("{} registered successfully", user.getUsername());
            return savedUser;
        });
    }

    @Override
    public List<User> addAll(final List<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    public User updateUser(final User user) {
        User newUser = userRepository.save(user);
        log.info("{} updated successfully", user.getUsername());
        return newUser;
    }


    @Override
    public boolean isExist(final String email) {
        Optional<User> potentialUser = userRepository.findByEmail(email);
        if (potentialUser.isPresent()) {
            log.info("User with username: {} exist", email);
            return true;
        }
        log.info("User with username: {} not exist", email);
        return false;
    }

    @Override
    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with username: " + email + " not found"));
    }

    @Override
    public UserDetailsService getUserDetailsService() {
        return this::getUserByEmail;
    }

    @Override
    public void enrollUser(User user, Course course) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                var userOptional = userRepository.findByEmail(user.getEmail());
                var userEntity = userOptional.orElseThrow(() -> new ObjectNotExistException("Нет пользователя с email: " + user.getEmail() + ", невозможно зачислить на курс"));
                userEntity.getCourseList().add(course);
                userRepository.save(userEntity);
            }
        });
    }

    @Override
    public void enrollUser(User user, UUID courseUUID) {
        var course = courseService.find(courseUUID);
//        transaction opens in method enrollUser() above
        enrollUser(user, course);
    }
}
