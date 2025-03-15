package org.example.blps_lab1.courseSignUp.service;

import lombok.RequiredArgsConstructor;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.models.UserModuleProgress;
import org.example.blps_lab1.courseSignUp.repository.UserModuleProgressRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserModuleProgressService {
    private final UserModuleProgressRepository userModuleProgressRepository;

    public boolean isModuleCompletedForUser(User user, Module module) {
        return userModuleProgressRepository.findByUserAndModule(user, module)
                .map(UserModuleProgress::getIsCompleted)
                .orElse(false);
    }
}
