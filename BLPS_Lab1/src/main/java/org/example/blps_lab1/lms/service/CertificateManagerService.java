package org.example.blps_lab1.lms.service;

import java.io.File;

import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.common.service.MinioService;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.service.CourseService;
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

    public void getCertificate(User user, Long courseId) {
        var course = courseService.getCourseById(courseId);
        
        try{
            var certificatePdf = certificateExporter.generateCertificate(course.getCourseName(), user.getEmail(), null);
            saveToMinio(user, course, certificatePdf);
            sendToUser(user, certificatePdf);
        }catch(Exception e){
            log.error("Error while creating the certificate", e);
            sendAboutException(user.getEmail());
        }
    }


    private void sendToUser(User user, File file){
        //TODO: нужна ручка для отправления файлов
        // emailService.createMimeMessageHelper(null, null)
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
        //TODO: нужно добавить ошибку 
        throw new RuntimeException("Ошибка сервиса при создании отчета");//ошибку эту оставить, необходимо, чтобы оно сработала даже в случае успешного письма пользователю об ошибке
    }
}
