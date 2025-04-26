package org.example.blps_lab1.core.ports.auth;

public interface UserEnrollmentService {
    void processEnrolment(Long applicationEnrollmentId, String applicationStatus);
}
