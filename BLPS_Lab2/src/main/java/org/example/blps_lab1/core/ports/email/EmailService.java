package org.example.blps_lab1.core.ports.email;

import org.example.blps_lab1.core.domain.course.Course;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public interface EmailService {
    MimeMessageHelper createMimeMessageHelper(String toEmail, String subject);

    void informAboutNewCourses(String toEmail,
                               String courseName,
                               BigDecimal price,
                               List<Course> additionalCourses);

    void informAboutModuleCompletion(String toEmail, String courseName, String moduleName);
    void rejectionMail(String toEmail, String courseName);
    void informMinioFailure(String toEmail);
    void sendCertificateToUser(String toEmail, File file);
}
