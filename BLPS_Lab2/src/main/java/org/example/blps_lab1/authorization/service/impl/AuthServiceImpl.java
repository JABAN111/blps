package org.example.blps_lab1.authorization.service.impl;

import java.util.List;

import org.example.blps_lab1.authorization.dto.ApplicationResponseDto;
import org.example.blps_lab1.authorization.dto.JwtAuthenticationResponse;
import org.example.blps_lab1.authorization.dto.LoginRequest;
import org.example.blps_lab1.authorization.dto.RegistrationRequestDto;
import org.example.blps_lab1.authorization.exception.AuthorizeException;
import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.authorization.service.CompanyService;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.common.exceptions.FieldNotSpecifiedException;

import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.config.security.services.JwtService;
import org.example.blps_lab1.lms.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private CompanyService companyService;
    private CourseService courseService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final EmailService emailService;
    private final TransactionTemplate transactionTemplate;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public AuthServiceImpl(CompanyService companyService, CourseService courseService, PasswordEncoder passwordEncoder, JwtService jwtService, UserService userService, ApplicationService applicationService, EmailService emailService, EntityManager em, PlatformTransactionManager transactionManager) {
        this.companyService = companyService;
        this.courseService = courseService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
        this.applicationService = applicationService;
        this.emailService = emailService;
        this.transactionManager = transactionManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public ApplicationResponseDto signUp(RegistrationRequestDto request) {
        return transactionTemplate.execute(status -> {
            var resultBuilder = ApplicationResponseDto.builder();
            var userBuilder = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .company(null)
                    .role(Role.CASUAL_STUDENT)
                    .password(passwordEncoder.encode(request.getPassword()));

            log.info(userBuilder.build().getPassword());
            // NOTE: if company is specified, user is legal entity
            if (request.getCompanyName() != null) {
                log.info("company is specified");

                if (!companyService.isExist(request.getCompanyName())) {
                    log.warn("company with name: {} not found", request.getCompanyName());

//                    TODO сюда надо вкорячить защиту от ебаного зависания email
//                    dev режим без email, либо замоканный email?
                    emailService.informAboutCompanyProblem(request.getEmail(),
                            request.getCompanyName());
                    throw new ObjectNotExistException(
                            "Компания с именем: " + request.getCompanyName() + " не зарегистрирована");
                }
                var companyEntity = companyService.getByName(request.getCompanyName());
                userBuilder.company(companyEntity);
                userBuilder.role(Role.LEGAL_COMPANY);
            }


            if (request.getCourseUUID() == null) {
                log.warn("course id is not specified, request: {}", request);
                throw new FieldNotSpecifiedException("Не указан id курса");
            }
            if (!courseService.isExist(request.getCourseUUID())) {
                log.warn("Course with id: {} not found", request.getCourseUUID());
                throw new ObjectNotExistException("Курс с id: " + request.getCourseUUID() + " не найден");
            }

            var courseEntity = courseService.getCourseByUUID(request.getCourseUUID());
            userBuilder.courseList(List.of(courseEntity));

            resultBuilder.description(courseEntity.getCourseDescription());
            resultBuilder.price(courseEntity.getCoursePrice());

            var user = userBuilder.build();

            if (userService.isExist(user.getUsername())) {
                log.warn("User with username: {} exist", user.getUsername());
                throw new AuthorizeException("Пользователь с именем: " + user.getUsername() +
                        " уже существует");
            }
            userService.add(user);

            var jwt = jwtService.generateToken(user);
            resultBuilder.jwt(new JwtAuthenticationResponse(jwt));

            applicationService.add(request.getCourseUUID(), user);

            return resultBuilder.build();
        });
    }


    @Override
    public JwtAuthenticationResponse signIn(LoginRequest request) {
        return transactionTemplate.execute(status -> {
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new FieldNotSpecifiedException("Поле email обязательное");
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new FieldNotSpecifiedException("Поле password обязательное");
            }

            User userEntity;
            try {
                userEntity = userService.getUserByEmail(request.getEmail());
                log.debug("Stored hash: {}", userEntity.getPassword());

                if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
                    throw new AuthorizeException("Пароль указан неверно");
                }
            } catch (UsernameNotFoundException e) {
                throw new AuthorizeException("Пользователя с заданным email не существует");
            }

            var jwt = jwtService.generateToken(userEntity);
            return new JwtAuthenticationResponse(jwt);
        });
    }
    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();

            return userService.getUserByEmail(username);
        } else {
            throw new AuthorizeException("Текущий пользователь не авторизован");
        }
    }
}
