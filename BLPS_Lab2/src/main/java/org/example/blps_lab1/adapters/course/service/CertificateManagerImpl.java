package org.example.blps_lab1.adapters.course.service;

import java.io.File;
import java.util.UUID;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.exception.common.ObjectNotExistException;
import org.example.blps_lab1.core.domain.course.Course;
import org.example.blps_lab1.core.domain.course.CourseProgress;
import org.example.blps_lab1.core.domain.course.CourseProgressId;
import org.example.blps_lab1.adapters.db.course.CourseProgressRepository;
import org.example.blps_lab1.core.ports.course.CertificateGenerator;
import org.example.blps_lab1.core.ports.course.CertificateManager;
import org.example.blps_lab1.core.ports.course.CourseService;
import org.example.blps_lab1.core.ports.course.UserModuleProgressService;
import org.example.blps_lab1.core.ports.email.EmailService;
import org.example.blps_lab1.core.ports.sss.SimpleStorageService;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class CertificateManagerImpl implements CertificateManager {
    private CertificateGenerator certificateGenerator;
    private CourseService courseService;
    private SimpleStorageService simpleStorageService;
    private EmailService emailService;
    private CourseProgressRepository courseProgressRepository;
    private UserModuleProgressService userModuleProgressService;

    @Override
    public void getCertificate(User user, UUID courseUUID) {
        var course = courseService.getCourseByUUID(courseUUID);
        if (course.getModules() == null || course.getModules().isEmpty()) {
            throw new ObjectNotExistException("Курс не сформирован до конца в нём отсутствуют модули");
        }

//        FIXME тут было в попыхах выпилине проверка. Нужно вернуть и починить ее
        boolean allModulesCompleted = course.getModules().stream()
                .allMatch(module -> userModuleProgressService.isModuleCompletedForUser(user, module));

        CourseProgress courseProgress = courseProgressRepository.findByUserAndCourse(user, course)
                .orElse(new CourseProgress(new CourseProgressId(course.getCourseUUID(), user.getId()), course, user, 0));

        courseProgressRepository.save(courseProgress);

        try {
            var certificatePdf = certificateGenerator.generateCertificate(course.getCourseName(), user.getEmail(), null);
            saveToSimpleStorageService(user, course, certificatePdf);
            emailService.sendCertificateToUser(user.getEmail(), certificatePdf);
        } catch (Exception e) {
            log.error("Error while creating the certificate", e);
            sendAboutException(user.getEmail());
        }
    }

    private void saveToSimpleStorageService(User user, Course course, File file) {
        StringBuilder filename = new StringBuilder();
        filename.append(user.getEmail()).append(course.getCourseName());
        try {
            simpleStorageService.uploadFile(user.getUsername(), filename.toString(), file);
        } catch (Exception e) {
            log.error("Error while uploading file to minio");
        }
    }

    private void sendAboutException(String email) {
        emailService.informMinioFailure(email);
        throw new RuntimeException("Ошибка сервиса при создании отчета");//ошибку эту оставить, необходимо, чтобы оно сработала даже в случае успешного письма пользователю об ошибке
    }
}
