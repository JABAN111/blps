package org.example.blps_lab1.adapters.course.service;

import lombok.RequiredArgsConstructor;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.course.Module;
import org.example.blps_lab1.core.domain.course.UserModuleProgress;
import org.example.blps_lab1.adapters.db.course.UserModuleProgressRepository;
import org.example.blps_lab1.core.ports.course.UserModuleProgressService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserModuleProgressServiceImpl implements UserModuleProgressService {
    private final UserModuleProgressRepository userModuleProgressRepository;

    @Override
    public Boolean isModuleCompletedForUser(User user, Module module) {
        return userModuleProgressRepository.findByUserAndModule(user, module)
                .map(UserModuleProgress::getIsCompleted)
                .orElse(false);
    }
}
