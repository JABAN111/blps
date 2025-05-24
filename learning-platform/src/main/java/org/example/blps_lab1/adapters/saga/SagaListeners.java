package org.example.blps_lab1.adapters.saga;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.adapters.db.saga.FailureRecordRepository;
import org.example.blps_lab1.adapters.saga.events.failures.CertificateGenerationFailedEvent;
import org.example.blps_lab1.adapters.saga.events.failures.FileUploadFailedEvent;
import org.example.blps_lab1.adapters.saga.events.success.CertificateGeneratedEvent;
import org.example.blps_lab1.adapters.saga.events.success.CertificateSentEvent;
import org.example.blps_lab1.adapters.saga.events.success.CourseCompletedEvent;
import org.example.blps_lab1.adapters.saga.events.success.FileUploadedEvent;
import org.example.blps_lab1.adapters.sss.SimpleStorageServiceWithRetry;
import org.example.blps_lab1.configuration.KafkaUser;
import org.example.blps_lab1.configuration.MessageProducer;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.example.blps_lab1.core.domain.saga.FailureRecord;
import org.example.blps_lab1.core.domain.saga.SagaFailedStep;
import org.example.blps_lab1.core.ports.course.CertificateGenerator;
import org.example.blps_lab1.core.ports.email.EmailService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@AllArgsConstructor
public class SagaListeners {
    private final CertificateGenerator certificateGenerator;
    private final SimpleStorageServiceWithRetry simpleStorageServiceWithRetry;
    private final MessageProducer messageProducer;
    private final EmailService emailService;
    private final FailureRecordRepository failureRecordRepository;
    private final ApplicationEventPublisher publisher;


    @EventListener
    public void handle(CourseCompletedEvent ev) {
        try {
            File pdf = certificateGenerator.generateCertificate(ev.getCourse().getName(), ev.getUser().getUsername(), null);
            publisher.publishEvent(new CertificateGeneratedEvent(ev.getUser(), ev.getCourse(), pdf));
        } catch (Exception ex) {
            publisher.publishEvent(new CertificateGenerationFailedEvent(ev.getUser(), ev.getCourse(), ex));
        }
    }

    @EventListener
    public void handle(CertificateGeneratedEvent ev) {
        try {
            simpleStorageServiceWithRetry.uploadWithRetry(ev.getUser().getUsername(), ev.getUser().getUsername() + ev.getCourse().getName(), ev.getPdf());
            publisher.publishEvent(new FileUploadedEvent(ev.getUser(), ev.getCourse(), ev.getPdf()));
        } catch (Exception ex) {
            publisher.publishEvent(new FileUploadFailedEvent(ev.getUser(), ev.getCourse(), ex));
        }
    }

    @EventListener
    public void handle(FileUploadedEvent ev) {
        var user = ev.getUser();
        messageProducer.sendMessage("reg-users", new KafkaUser(user.getUsername(), user.getUsername(), user.getPassword()));
        publisher.publishEvent(new CertificateSentEvent(ev.getUser(), ev.getPdf()));
    }

    @EventListener
    public void handle(CertificateSentEvent ev) {
        emailService.sendCertificateToUser(ev.getUser().getUsername(), ev.getPdf());
    }

    @EventListener
    public void handle(CertificateGenerationFailedEvent ev) {
        log.error("Saga step failed: generation", ev.getException());
        saveFail(ev.getUser().getUsername() ,ev.getUser().getPassword(), ev.getCourse(), ev.getException().getMessage(), SagaFailedStep.CERTIFICATE_GENERATE_FAIL);
    }

    @EventListener
    public void handle(FileUploadFailedEvent ev) {
        log.error("Saga step failed: upload", ev.getException());
        saveFail(ev.getUser().getUsername(), ev.getUser().getPassword(),ev.getCourse(), ev.getException().getMessage(), SagaFailedStep.FILE_UPLOAD_FAIL);
    }

    private void saveFail(String username,String userPassword, NewCourse course, String exceptionMessage, SagaFailedStep step) {
        var failRecord = FailureRecord
                .builder()
                .username(username)
                .userPassword(userPassword)
                .course(course)
                .sagaFailedStep(step)
                .errorMessage(exceptionMessage)
                .build();
        failureRecordRepository.save(failRecord);
    }
}
