package org.example.blps_lab1;

import java.math.BigDecimal;
import java.util.*;



import org.example.blps_lab1.authorization.models.Company;
import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.CompanyService;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.models.Topic;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RandomDataGeneratorTEST implements ApplicationRunner {
    private CompanyService companyService;
    private CourseService courseService;
    private UserService userService;
    private EntityManager em;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {  
        // NOTE: Генерация случайных данных для тестирования
        // Выпилить перед показом
        List<Company> companies = new ArrayList<>();
        List<Course> courses = new ArrayList<>();
        List<User> users = new ArrayList<>();
        
        for (int i = 0; i < 1_000; i++) {
            
            var course = Course.builder()
            .courseName(UUID.randomUUID().toString())
            .coursePrice(BigDecimal.valueOf(new Random().nextDouble()))
            .courseDescription(UUID.randomUUID().toString())
            .courseDuration(new Random().nextInt())
            .withJobOffer(new Random().nextBoolean())
            .topicName(Topic.ANALYTICS)
            .build();

            var user = User.builder()
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber("8-800-555-35-35")
            .role(Role.CASUAL_STUDENT)
            .password(UUID.randomUUID().toString())
            .courseList(List.of(course))
            .build();

            Company company = Company.builder()
                    .companyName(UUID.randomUUID().toString())
                    .user(user)
                    .build();
            

            users.add(user);
        
            companies.add(company);

            courses.add(course);
        }

        System.out.println("Companies: " + companies.size());
        System.out.println("Courses: " + courses.size());
        System.out.println("Users: " + users.size());
        
        userService.addAll(users);
        em.flush();
        companyService.saveAll(companies);
        courseService.saveAll(courses);


        System.out.println("all saved");

    }
}
