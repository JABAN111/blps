package org.example.blps_lab1.admin.service;

import java.util.Objects;

import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.UserService;

import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class AdminPanelService {

    private final UserService userService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public AdminPanelService(UserService userService, PlatformTransactionManager platformTransactionManager) {
        this.userService = userService;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    public void updateRole(String userEmail, String role) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                if (Objects.isNull(role) || Objects.isNull(userEmail)) {
                    throw new IllegalArgumentException("Поля email и role обязательны");
                }
                Role roleToSet;
                try {
                    roleToSet = Role.valueOf(role);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid argument, got: {}", role, e);
                    throw new IllegalArgumentException("Неверно указана роль");
                }

                var user = userService.getUserByEmail(userEmail);
                if (Objects.nonNull(user)) {
                    user.setRole(roleToSet);
                    userService.updateUser(user);
                }
            }
        });
    }
}
