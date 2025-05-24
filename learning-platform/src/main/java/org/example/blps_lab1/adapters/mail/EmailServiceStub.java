package org.example.blps_lab1.adapters.mail;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.example.blps_lab1.core.ports.email.EmailService;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("EmailService")
@Slf4j
@Profile("dev")
public class EmailServiceStub implements EmailService {

    private void simulateWorking(){
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            log.error("InterruptedException has happened: {}", e.getMessage());
        }
    }

    @Override
    public MimeMessageHelper createMimeMessageHelper(String toEmail, String subject) {
        simulateWorking();
        return null;
    }

    @Override
    public void informAboutNewCourses(String toEmail, String courseName, BigDecimal price, List<NewCourse> additionalCourses) {
        simulateWorking();

    }

    @Override
    public void informAboutModuleCompletion(String toEmail, String courseName, String moduleName) {
        simulateWorking();

    }

    @Override
    public void rejectionMail(String toEmail, String courseName) {
        simulateWorking();

    }

    @Override
    public void informMinioFailure(String toEmail) {
        simulateWorking();

    }

    @Override
    public void sendCertificateToUser(String toEmail, File file) {
        simulateWorking();

    }
}
