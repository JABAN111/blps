package org.example.blps_lab1;

import java.math.BigDecimal;
import java.util.*;

import org.example.blps_lab1.adapters.admin.AdminPanelServiceImpl;
import org.example.blps_lab1.adapters.auth.dto.RegistrationRequestDto;
import org.example.blps_lab1.core.ports.admin.AdminPanelService;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.domain.course.Course;
import org.example.blps_lab1.core.domain.course.Topic;
import org.example.blps_lab1.core.ports.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;


/**
 * Класс для генерации входных данных, в частности 0 админа
 */
@Service
public class FirstDataGenerator implements ApplicationRunner {

    private final AuthService authService;
    private final AdminPanelService adminPanelService;
    private final CourseService courseService;

    @Value("${app.admin.username}")
    private String adminLogin;
    @Value("${app.admin.password}")
    private String adminPass;

    @Autowired
    public FirstDataGenerator(AuthService authService, AdminPanelService adminPanelService, CourseService courseService) {
        this.authService = authService;
        this.adminPanelService = adminPanelService;
        this.courseService = courseService;
    }


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // генерация нулевого админа
        RegistrationRequestDto adminUserRequest = new RegistrationRequestDto();
        adminUserRequest.setEmail(adminLogin);
        adminUserRequest.setPassword(adminPass);
        adminUserRequest.setLastName(adminLogin);
        adminUserRequest.setFirstName(adminLogin);
        adminUserRequest.setPhoneNumber("+7800553535");

        authService.signUp(adminUserRequest);
        adminPanelService.updateRole(adminUserRequest.getEmail(), "ROLE_ADMIN");

//         генерация нулевого пользователя
        RegistrationRequestDto simpleUserReq = new RegistrationRequestDto();
        simpleUserReq.setEmail("jaba@jaba.jaba");
        simpleUserReq.setPassword("jaba");
        simpleUserReq.setLastName("jaba");
        simpleUserReq.setFirstName("jaba");
        simpleUserReq.setPhoneNumber("+7800553535");
        authService.signUp(simpleUserReq);

        // генерация нулевого курса
        List<Course> courses = new ArrayList<>();
        var course = Course.builder()
                .courseName(UUID.randomUUID().toString())
                .coursePrice(BigDecimal.valueOf(new Random().nextDouble()))
                .courseDescription(UUID.randomUUID().toString())
                .courseDuration(new Random().nextInt())
                .withJobOffer(new Random().nextBoolean())
                .topicName(Topic.ANALYTICS)
                .build();

        courses.add(course);
        courseService.addAll(courses);
    }
}
