package org.example.blps_lab1.adapters.auth.service;

import lombok.extern.slf4j.Slf4j;

import org.example.blps_lab1.core.domain.auth.UserXml;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.example.blps_lab1.core.ports.auth.UserService;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.example.blps_lab1.core.ports.db.UserDatabase;
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
public class UserServiceImpl implements UserService {
    private final UserDatabase userRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public UserServiceImpl(UserDatabase userRepository, PlatformTransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }


    @Override
    public UserXml add(final UserXml user) {
        var userEntity = transactionTemplate.execute(status -> {
            user.setPassword(user.getPassword());
            UserXml savedUser = userRepository.save(user);
            log.info("{} registered successfully", user.getUsername());
            return savedUser;
        });
        return userEntity;
    }


    @Override
    public UserXml updateUser(final UserXml user) {
        UserXml newUser = userRepository.save(user);
        log.info("{} updated successfully", user.getUsername());
        return newUser;
    }


    @Override
    public boolean isExist(final String email) {
        Optional<UserXml> potentialUser = userRepository.findByEmail(email);
        if (potentialUser.isPresent()) {
            log.info("User with username: {} exist", email);
            return true;
        }
        log.info("User with username: {} not exist", email);
        return false;
    }

    @Override
    public UserXml getUserByEmail(final String email) {
        return userRepository.findByEmail(email.trim()).orElseThrow(() -> new UsernameNotFoundException("User with username: " + email + " not found"));
    }

    @Override
    public UserDetailsService getUserDetailsService() {
        return this::getUserByEmail;
    }

    @Override
    public void enrollUser(UserXml user, NewCourse course) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                var userOptional = userRepository.findByEmail(user.getUsername());
                var userEntity = userOptional.orElseThrow(() -> new ObjectNotExistException("Нет пользователя с email: " + user.getUsername() + ", невозможно зачислить на курс"));
                userRepository.save(userEntity);
            }
        });
    }

}
