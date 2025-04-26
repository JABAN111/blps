package org.example.blps_lab1.core.ports.auth;

import org.example.blps_lab1.core.domain.auth.Application;
import org.example.blps_lab1.core.domain.auth.ApplicationStatus;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.auth.UserXml;

import java.util.UUID;

public interface ApplicationService {
    Application add(Long courseUUID);
    Application add(Long courseUUID, UserXml user);

    Application updateStatus(Long id, ApplicationStatus applicationStatus);

}
