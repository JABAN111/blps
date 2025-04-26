package org.example.blps_lab1.core.ports.course;

import org.example.blps_lab1.core.domain.auth.User;

import java.util.UUID;

public interface CertificateManager {
    void getCertificate(User user, UUID courseUUID);
}
