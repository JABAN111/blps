package org.example.blps_lab1.authorization.service.impl;

import java.util.UUID;

import org.example.blps_lab1.authorization.dto.ApplicationResponseDto;
import org.example.blps_lab1.authorization.dto.JwtAuthenticationResponse;
import org.example.blps_lab1.authorization.dto.LoginRequest;
import org.example.blps_lab1.authorization.dto.RegistrationRequestDto;
import org.example.blps_lab1.authorization.exception.AuthorizeException;
import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.common.exceptions.CourseNotExistException;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.AuthService;
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
// transactional OK
public class AuthServiceImpl implements AuthService {

    private CourseService courseService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final EmailService emailService; // FIXME: temporary killed, need to enable and check
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public AuthServiceImpl(CourseService courseService, PasswordEncoder passwordEncoder,
                           JwtService jwtService, UserService userService,
                           ApplicationService applicationService, EmailService emailService,
                           PlatformTransactionManager transactionManager) {
        this.courseService = courseService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
        this.applicationService = applicationService;
        this.emailService = emailService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Возвращает готового пользователя, собранного из <code>RegistrationRequestDto</code>
     *
     * @param request RegistrationRequestDto
     * @return {@link User}, которого можно сохранять в бд
     * @throws AuthorizeException, если пользователь с таким именем существует
     */
    private User getUserOrThrow(RegistrationRequestDto request) {
        var userBuilder = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .company(null)
                .role(Role.CASUAL_STUDENT)
                .password(passwordEncoder.encode(request.getPassword()));
        var user = userBuilder.build();

        if (userService.isExist(user.getUsername())) {
            log.warn("User with username: {} exist", user.getUsername());
            throw new AuthorizeException("Пользователь с именем: " + user.getUsername() +
                    " уже существует");
        }
        return user;
    }

    /**
     * Регистрация пользователя без записи на курс
     *
     * @param request включает в себя поля для регистрации поля
     * @return обертку с JWT токеном внутри
     */
    @Override
    public JwtAuthenticationResponse signUp(RegistrationRequestDto request) {
        return transactionTemplate.execute(status -> {
            var user = getUserOrThrow(request);
            userService.add(user);
            var jwt = jwtService.generateToken(user);
            return new JwtAuthenticationResponse(jwt);
        });
    }

    /**
     * Регистрация пользователя с записью на курс
     *
     * @param request    включает в себя поля для регистрации поля
     * @param courseUUID uuid курса, на который записывается пользователь.
     *                   Если курса не существует, выбрасывает ошибку {@link CourseNotExistException}
     *                   Если uuid не указан, выбрасывает ошибку {@link FieldNotSpecifiedException}
     * @return {@link ApplicationResponseDto}, который включает в себя
     * jwt токен {@link JwtAuthenticationResponse} и информацию о заявке(цену и описание)
     */
    @Override
    public ApplicationResponseDto signUp(RegistrationRequestDto request, UUID courseUUID) {
        return transactionTemplate.execute(status -> {
            var resultBuilder = ApplicationResponseDto.builder();
            var user = getUserOrThrow(request);
            if (courseUUID == null) {
                log.warn("course id is not specified, request: {}", request);
                throw new FieldNotSpecifiedException("Не указан id курса");
            }
            try {
                courseService.getCourseByUUID(courseUUID);
            } catch (ObjectNotExistException e) {
                log.warn("course with uuid: {} not found", courseUUID);
                throw new CourseNotExistException("ошибка при создании заявки: данного курса больше не существует");
            }
            var userEntity = userService.add(user);
            applicationService.add(courseUUID, userEntity);

            var jwt = jwtService.generateToken(user);
            resultBuilder.jwt(new JwtAuthenticationResponse(jwt));
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
