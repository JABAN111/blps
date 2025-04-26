package org.example.blps_lab1.core.ports.auth;

import org.example.blps_lab1.core.domain.auth.Application;
import org.example.blps_lab1.core.domain.auth.ApplicationStatus;
import org.example.blps_lab1.core.domain.auth.User;

import java.util.UUID;

public interface ApplicationService {
    Application add(UUID courseUUID);
    Application add(UUID courseUUID, User user);

    Application updateStatus(Long id, ApplicationStatus applicationStatus);

}
