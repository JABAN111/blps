package org.example.blps_lab1.adapters.saga;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.blps_lab1.adapters.db.saga.FailureRecordRepository;
import org.example.blps_lab1.adapters.saga.events.success.FileUploadedEvent;
import org.example.blps_lab1.adapters.sss.SimpleStorageServiceWithRetry;
import org.example.blps_lab1.core.domain.auth.UserXml;
import org.example.blps_lab1.core.domain.saga.FailureRecord;
import org.example.blps_lab1.core.domain.saga.SagaFailedStep;
import org.example.blps_lab1.core.ports.course.CertificateGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RecoveryService {
    private final FailureRecordRepository failureRepo;
    private final ApplicationEventPublisher publisher;
    private final CertificateGenerator certificateGenerator;
    private final SimpleStorageServiceWithRetry simpleStorageServiceWithRetry;


    @Scheduled(fixedRate = 10_000*3)
    public void recoverFileUploads() {
        List<FailureRecord> failed = failureRepo.findAllBySagaFailedStep(SagaFailedStep.FILE_UPLOAD_FAIL);
        List<FailureRecord> toDelete = new ArrayList<>();
        for (var f : failed) {
            try {
                File pdf = certificateGenerator.generateCertificate(f.getCourse().getName(), f.getUsername(), null);
                simpleStorageServiceWithRetry.uploadWithRetry(f.getUsername(), f.getUsername() + f.getCourse().getName(), pdf);
                var user = new UserXml();
                user.setUsername(f.getUsername());
                user.setPassword(f.getUserPassword());
                publisher.publishEvent(new FileUploadedEvent(user, f.getCourse(), pdf));
                toDelete.add(f);
            }catch (Exception ignored) {}
        }
        failureRepo.deleteAll(toDelete);
    }
}
