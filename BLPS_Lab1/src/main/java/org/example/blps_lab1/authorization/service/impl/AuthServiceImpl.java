package org.example.blps_lab1.authorization.service.impl;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.example.blps_lab1.authorization.dto.ApplicationResponseDto;
import org.example.blps_lab1.authorization.dto.JwtAuthenticationResponse;
import org.example.blps_lab1.authorization.dto.LoginRequest;
import org.example.blps_lab1.authorization.dto.RegistrationRequestDto;
import org.example.blps_lab1.authorization.models.Company;
import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.courseSignUp.service.CourseService;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.AuthService;
import org.example.blps_lab1.authorization.service.CompanyService;
import org.example.blps_lab1.authorization.service.UserService;
import org.example.blps_lab1.common.exceptions.FieldNotSpecifiedException;
import org.example.blps_lab1.common.exceptions.ObjectAlreadyExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.config.security.services.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private CompanyService companyService;
    private CourseService courseService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public ApplicationResponseDto signUp(RegistrationRequestDto request) {
        var resultBuilder = ApplicationResponseDto.builder(); 

        var userBuilder = User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .phoneNumber(request.getPhoneNumber())
        .company(null)
        .role(Role.CASUAL_STUDENT)
        .password(passwordEncoder.encode(request.getPassword()));


        if (request.getCompanyName() != null) {//NOTE: if company is specifed, user is legal entity
            if(companyService.isExist(request.getCompanyName())){
                log.warn("Company with name: {} not found", request.getCompanyName());
                //FIXME: send email to user
                throw new NotImplementedException("Необходимо добавить, что если компания не зарегестрирована, то ошибка"+ 
                "присылается пользователю в виде письма на почту");
            }
            var companyEntity = companyService.getByName(request.getCompanyName());
            userBuilder.company(companyEntity);
            userBuilder.role(Role.LEGAL_COMPANY);
        }

        if (request.getCourseId() == null) {
            log.warn("Course id is not specified", request);
            throw new FieldNotSpecifiedException("Не указан id курса");
        }
        if(!courseService.isExist(request.getCourseId())){
            log.warn("Course with id: {} not found", request.getCourseId());
            throw new ObjectNotExistException("Курс с id: " + request.getCourseId() + " не найден");
        }

        var courseEntity = courseService.getCourseById(request.getCourseId());
        userBuilder.courseList(List.of(courseEntity));
        
        resultBuilder.description(courseEntity.getCourseDescription());
        resultBuilder.price(courseEntity.getCoursePrice());
        
        
        
        var user = userBuilder.build();

        if(userService.isExist(user.getUsername())){
            log.warn("User with username: {} exist", user.getUsername());
            throw new ObjectAlreadyExistException("Пользователь с именем: " + user.getUsername() + " уже существует");
        }
        userService.add(user);

        var jwt = jwtService.generateToken(user);
        resultBuilder.jwt(new JwtAuthenticationResponse(jwt));

        return resultBuilder.build();
    }

    @Override
    public JwtAuthenticationResponse signIn(LoginRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'signIn'");
    }
    
}
