package org.example.blps_lab1.admin.service;

import java.util.Objects;

import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.authorization.service.UserService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class AdminPanelService {

    private UserService userService;

    public void updateRole(String userEmail, String role) {
        if(Objects.isNull(role) || Objects.isNull(userEmail)){
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

}
