package org.example.blps_lab1.test;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.dto.ApplicationResponseDto;
import org.example.blps_lab1.authorization.dto.JwtAuthenticationResponse;
import org.example.blps_lab1.authorization.dto.RegistrationRequestDto;
import org.example.blps_lab1.authorization.exception.AuthorizeException;
import org.example.blps_lab1.authorization.models.Application;
import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.authorization.service.impl.ApplicationService;
import org.example.blps_lab1.config.security.services.JwtService;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SchedUserServ {

    private final ApplicationService applicationService;
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;
    private final TransactionTemplate transactionTemplate;
    private final AuthService authService;
    private CourseService courseService;
    private final JwtService jwtService;
    private static boolean dumb = true;


    @Autowired
    public SchedUserServ(JdbcTemplate jdbcTemplate,
                         UserService userService,
                         AuthService authService,
                         PlatformTransactionManager transactionManager,
                         CourseService courseService,
                         JwtService jwtService,
                         ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
        this.jwtService = jwtService;
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
        this.authService = authService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.courseService = courseService;
    }

    // Программное управление транзакциями
    public void create(String name) {
        var res = transactionTemplate.execute(status -> {
            var resultBuilder = ApplicationResponseDto.builder();
            RegistrationRequestDto request = new RegistrationRequestDto();
            request.setEmail(UUID.randomUUID().toString());
            request.setPassword(UUID.randomUUID().toString());
            request.setLastName(UUID.randomUUID().toString());
            request.setFirstName(UUID.randomUUID().toString());
            request.setPhoneNumber("_73219211223");
            request.setCourseId(1L);

            if (!dumb)
                request.setCompanyName(UUID.randomUUID().toString());
            if (dumb) {
                dumb = false;
            }
            System.out.println("отправляет регистрацию");
            var d = authService.signUp(request);
            System.out.println("закончили?");

            var userBuilder = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .company(null)
                    .role(Role.CASUAL_STUDENT)
                    .password((request.getPassword()));


            var courseEntity = courseService.getCourseById(request.getCourseId());
            userBuilder.courseList(List.of(courseEntity));

            resultBuilder.description(courseEntity.getCourseDescription());
            resultBuilder.price(courseEntity.getCoursePrice());


            //last
            var user = userBuilder.build();
//            if (userService.isExist(user.getUsername())) {
//                log.warn("User with username: {} exist", user.getUsername());
//                throw new AuthorizeException("Пользователь с именем: " + user.getUsername() +
//                        " уже существует");
//            }
//            userService.add(user);

            var jwt = jwtService.generateToken(user);
            resultBuilder.jwt(new JwtAuthenticationResponse(jwt));

//            applicationService.add(request.getCourseId(), user);

//                var res = authService.signUp(r);
//                System.out.println(res);
//                var user = new User();
//                user.setPassword("somequitegoodpwd");
//                user.setEmail(UUID.randomUUID().toString());
//                user.setFirstName(UUID.randomUUID().toString());
//                user.setLastName(UUID.randomUUID().toString());
//                user.setPhoneNumber("89991875292");
//                user.setRole(Role.ROLE_ADMIN);
//                userService.add(user);
//
//                var user1 = new User();
//                user1.setPassword("somequitegoodpwd");
//                user1.setEmail(UUID.randomUUID().toString());
//                user1.setFirstName(UUID.randomUUID().toString());
//                user1.setLastName(UUID.randomUUID().toString());
//                user1.setPhoneNumber("89991875292");
//                user1.setRole(Role.ROLE_ADMIN);
//                userService.add(user1);
            return "success: " + d.toString();
        });
        System.out.println("res: " + res);
    }

    public List<String> getAll() {
        return this.jdbcTemplate.queryForList("SELECT NAME FROM UNI", String.class);
    }
}
