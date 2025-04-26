package org.example.blps_lab1.core.ports.course;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.course.Module;

public interface UserModuleProgressService {
    Boolean isModuleCompletedForUser(User user, Module module);
}
