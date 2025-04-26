package org.example.blps_lab1;

import java.math.BigDecimal;
import java.util.*;

import org.example.blps_lab1.adapters.admin.AdminPanelServiceImpl;
import org.example.blps_lab1.adapters.auth.dto.RegistrationRequestDto;
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
    private final AdminPanelServiceImpl adminPanelService;
    private final CourseService courseService;

    @Value("${app.admin.password}")
    private String adminPass;
    @Value("${app.admin.password}")
    private String adminLogin;

    @Autowired
    public FirstDataGenerator(AuthService authService, AdminPanelServiceImpl adminPanelService, CourseService courseService) {
        this.authService = authService;
        this.adminPanelService = adminPanelService;
        this.courseService = courseService;
    }


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        RegistrationRequestDto adminUserRequest = new RegistrationRequestDto();
        adminUserRequest.setEmail(adminLogin);
        adminUserRequest.setPassword(adminPass);
        adminUserRequest.setLastName(adminLogin);
        adminUserRequest.setFirstName(adminLogin);
        adminUserRequest.setPhoneNumber("+7800553535");

        authService.signUp(adminUserRequest);//пользователь зареган
        adminPanelService.updateRole(adminUserRequest.getEmail(), "ROLE_ADMIN");

        RegistrationRequestDto simpleUserReq = new RegistrationRequestDto();
        simpleUserReq.setEmail("jaba@jaba.jaba");
        simpleUserReq.setPassword("jaba");
        simpleUserReq.setLastName("jaba");
        simpleUserReq.setFirstName("jaba");
        simpleUserReq.setPhoneNumber("+7800553535");

        authService.signUp(simpleUserReq);//пользователь зареган



        // NOTE: Генерация случайных данных для тестирования
        // Выпилить перед показом
//        List<Company> companies = new ArrayList<>();
        List<Course> courses = new ArrayList<>();
//        List<User> users = new ArrayList<>();
//
//
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
