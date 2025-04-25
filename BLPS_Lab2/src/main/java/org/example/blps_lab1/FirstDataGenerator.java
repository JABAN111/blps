package org.example.blps_lab1;

import java.math.BigDecimal;
import java.util.*;

import lombok.NoArgsConstructor;
import org.example.blps_lab1.admin.service.AdminPanelService;
import org.example.blps_lab1.authorization.dto.ApplicationResponseDto;
import org.example.blps_lab1.authorization.dto.RegistrationRequestDto;
import org.example.blps_lab1.authorization.models.Company;
import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.authorization.service.CompanyService;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.models.Topic;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;


/**
 * Класс для генерации входных данных, в частности 0 админа
 */
@Service
public class FirstDataGenerator implements ApplicationRunner {

    private final AuthService authService;
    private final AdminPanelService adminPanelService;

    @Value("${app.admin.password}")
    private String adminPass;
    @Value("${app.admin.password}")
    private String adminLogin;

    @Autowired
    public FirstDataGenerator(AuthService authService, AdminPanelService adminPanelService) {
        this.authService = authService;
        this.adminPanelService = adminPanelService;
    }


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setEmail(adminLogin);
        request.setPassword(adminPass);
        request.setLastName(adminLogin);
        request.setFirstName(adminLogin);
        request.setPhoneNumber("+7800553535");

        authService.signUp(request);//пользователь зареган
//        adminPanelService.updateRole(rdto.getEmail(), "ROLE_ADMIN");

        // NOTE: Генерация случайных данных для тестирования
        // Выпилить перед показом
//        List<Company> companies = new ArrayList<>();
//        List<Course> courses = new ArrayList<>();
//        List<User> users = new ArrayList<>();
//
//
//        var course = Course.builder()
//                .courseName(UUID.randomUUID().toString())
//                .coursePrice(BigDecimal.valueOf(new Random().nextDouble()))
//                .courseDescription(UUID.randomUUID().toString())
//                .courseDuration(new Random().nextInt())
//                .withJobOffer(new Random().nextBoolean())
//                .topicName(Topic.ANALYTICS)
//                .build();
//
//        var user = User.builder()
//                .firstName(UUID.randomUUID().toString())
//                .lastName(UUID.randomUUID().toString())
//                .email(UUID.randomUUID().toString())
//                .phoneNumber("8-800-555-35-35")
//                .role(Role.CASUAL_STUDENT)
//                .password(UUID.randomUUID().toString())
//                .courseList(List.of(course))
//                .build();
//
//        Company company = Company.builder()
//                .companyName(UUID.randomUUID().toString())
//                .user(user)
//                .build();
//
//        users.add(user);
//
//        companies.add(company);
//
//        courses.add(course);
//
//
//        System.out.println("Companies: " + companies.size());
//        System.out.println("Courses: " + courses.size());
//        System.out.println("Users: " + users.size());
//
//        userService.addAll(users);
//        em.flush();
//        companyService.saveAll(companies);
//        courseService.saveAll(courses);
//
//        var us = new User();
//        us.setEmail("jaba@jaba.jaba");
//        us.setPassword("jaba");
//        us.setLastName("jaba");
//        us.setFirstName("jaba");
//        us.setRole(Role.CASUAL_STUDENT);
//        us.setPassword(passwordEncoder.encode("jaba"));
//        userService.add(us);
//        System.out.println("all saved");

    }
}
