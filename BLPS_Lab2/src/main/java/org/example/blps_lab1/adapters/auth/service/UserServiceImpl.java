package org.example.blps_lab1.adapters.auth.service;

import lombok.extern.slf4j.Slf4j;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.adapters.db.auth.UserRepository;
import org.example.blps_lab1.core.ports.auth.UserService;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.example.blps_lab1.core.domain.course.Course;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Service
@Slf4j
// transactional OK
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PlatformTransactionManager transactionManager) {
        this.userRepository = userRepository;
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
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                var userOptional = userRepository.findByEmail(user.getEmail());
                var userEntity = userOptional.orElseThrow(() -> new ObjectNotExistException("Нет пользователя с email: " + user.getEmail() + ", невозможно зачислить на курс"));
                userEntity.getCourseList().add(course);
                userRepository.save(userEntity);
            }
        });
    }

}
