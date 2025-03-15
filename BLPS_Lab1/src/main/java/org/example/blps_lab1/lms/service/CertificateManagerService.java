package org.example.blps_lab1.lms.service;

import java.io.File;

import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.service.MinioService;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.models.CourseProgress;
import org.example.blps_lab1.courseSignUp.models.CourseProgressId;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.repository.CourseProgressRepository;
import org.example.blps_lab1.courseSignUp.repository.CourseRepository;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.example.blps_lab1.courseSignUp.service.UserModuleProgressService;
import org.example.blps_lab1.export.certificate.CertificateExporter;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class CertificateManagerService {
    private CertificateExporter certificateExporter;
    private CourseService courseService;
    private MinioService minioService;
    private EmailService emailService;
    private CourseProgressRepository courseProgressRepository;
    private UserModuleProgressService userModuleProgressService;

    public void getCertificate(User user, Long courseId) {
        var course = courseService.getCourseById(courseId);
        if(course.getModules() == null || course.getModules().isEmpty()){
            throw new ObjectNotExistException("Курс не сформирован до конца в нём отсутствуют модули");
        }

        boolean allModulesCompleted = course.getModules().stream()
                .allMatch(module -> userModuleProgressService.isModuleCompletedForUser(user, module));


        if(!allModulesCompleted){
            throw new RuntimeException("Курс не завершен, так как не все модули пройдены");
        }

        CourseProgress courseProgress = courseProgressRepository.findByUserAndCourse(user, course)
                .orElse(new CourseProgress(new CourseProgressId(user.getId(), course.getCourseId()), course, user, 0));

        courseProgressRepository.save(courseProgress);

        try{
            var certificatePdf = certificateExporter.generateCertificate(course.getCourseName(), user.getEmail(), null);
            saveToMinio(user, course, certificatePdf);
            emailService.sendCertificateToUser(user.getEmail(), certificatePdf);
        } catch(Exception e) {
            log.error("Error while creating the certificate", e);
            sendAboutException(user.getEmail());
        }
    }

    private void saveToMinio(User user, Course course, File file){
        StringBuilder filename = new StringBuilder();
        filename.append(user.getEmail()).append(course.getCourseName());
        try{
            minioService.uploadFile(user.getUsername(), filename.toString(), file);
        }catch(Exception e){
            log.error("Error while uploading file to minio");
        }
    }

    private void sendAboutException(String email){
        emailService.informMinioFailure(email);
        throw new RuntimeException("Ошибка сервиса при создании отчета");//ошибку эту оставить, необходимо, чтобы оно сработала даже в случае успешного письма пользователю об ошибке
    }
}
