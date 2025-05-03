package org.example.blps_lab1;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.adapters.admin.AdminPanelServiceImpl;
import org.example.blps_lab1.core.domain.course.Course;
import org.example.blps_lab1.core.domain.course.Topic;
import org.example.blps_lab1.core.ports.auth.AuthService;
import org.example.blps_lab1.core.ports.auth.UserService;
import org.example.blps_lab1.core.ports.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class Bootstrap implements ApplicationRunner {


    private final AuthService authService;
    private final AdminPanelServiceImpl adminPanelService;
    private final CourseService courseService;
    private final UserService userService;

    @Value("${app.admin.password}")
    private String adminPass;
    @Value("${app.admin.username}")
    private String adminLogin;

    @Autowired
    public Bootstrap(AuthService authService, AdminPanelServiceImpl adminPanelService, CourseService courseService, UserService userService) {
        this.authService = authService;
        this.adminPanelService = adminPanelService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            var course = Course.builder()
                    .courseName(UUID.randomUUID().toString())
                    .coursePrice(BigDecimal.valueOf(new Random().nextDouble()))
                    .courseDescription(UUID.randomUUID().toString())
                    .courseDuration(new Random().nextInt())
                    .withJobOffer(new Random().nextBoolean())
                    .topicName(Topic.ANALYTICS)
                    .build();

            courses.add(course);
        }
        courseService.addAll(courses);
        log.info("was saved: {}", courses.size());

    }
}
