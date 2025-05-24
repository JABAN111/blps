package org.example.blps_lab1.core.ports.auth;

import org.example.blps_lab1.core.domain.auth.Application;
import org.example.blps_lab1.core.domain.auth.ApplicationStatus;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.auth.UserXml;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    Application add(UUID courseUUID);
    Application add(UUID courseUUID, UserXml user);

    Application updateStatus(Long id, ApplicationStatus applicationStatus);

    List<Application> find(UUID courseUUID);

    void remove(List<Application> applicationList);
}
